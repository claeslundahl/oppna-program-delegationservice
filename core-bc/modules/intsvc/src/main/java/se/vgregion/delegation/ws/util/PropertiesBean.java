/**
 * 
 */
package se.vgregion.delegation.ws.util;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class PropertiesBean {

    private String certPass;
    private String certFileName;
    private String serverPort;
    private String mailServer;
    private String mailServerPort;
    private String regularExpressionClientCert;
    private boolean clientCertSecurityActive;

    public String getCertPass() {
        return certPass;
    }

    public void setCertPass(String certPass) {
        this.certPass = certPass;
    }

    public String getCertFileName() {
        return certFileName;
    }

    public void setCertFileName(String certPath) {
        this.certFileName = certPath;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public boolean isClientCertSecurityActive() {
        return clientCertSecurityActive;
    }

    public void setRegularExpressionClientCert(String regularExpressionClientCert) {
        this.regularExpressionClientCert = regularExpressionClientCert;
    }

    public String getRegularExpressionClientCert() {
        return regularExpressionClientCert;
    }

}
