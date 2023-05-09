package se.vgregion.delegation.delegation.view.directory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;

public class TrustStoreSslSocketFactory extends SocketFactory {

    private static final AtomicReference<TrustStoreSslSocketFactory> defaultFactory = new AtomicReference<>();

    private SSLSocketFactory sf;

    public TrustStoreSslSocketFactory() {
        init();
    }

    public void init() {
        Path path = Paths.get(ApplicationProperties.getInstance().getLdapCertificatePath());

        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(fileInputStream);

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, "password".toCharArray());
            keyStore.setCertificateEntry("ldap", certificate);
            keyStore.getCertificate("ldap");

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            sf = ctx.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SocketFactory getDefault() {
        final TrustStoreSslSocketFactory value = defaultFactory.get();
        if (value == null) {
            defaultFactory.compareAndSet(null, new TrustStoreSslSocketFactory());
            return defaultFactory.get();
        }
        return value;
    }

    @Override
    public Socket createSocket(final String s, final int i) throws IOException {
        return sf.createSocket(s, i);
    }

    @Override
    public Socket createSocket(final String s, final int i, final InetAddress inetAddress, final int i1) throws IOException {
        return sf.createSocket(s, i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(final InetAddress inetAddress, final int i) throws IOException {
        return sf.createSocket(inetAddress, i);
    }

    @Override
    public Socket createSocket(final InetAddress inetAddress, final int i, final InetAddress inetAddress1, final int i1) throws IOException {
        return sf.createSocket(inetAddress, i, inetAddress1, i1);
    }

}
