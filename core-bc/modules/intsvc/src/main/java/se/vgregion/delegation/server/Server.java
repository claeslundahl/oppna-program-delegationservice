/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package se.vgregion.delegation.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.Endpoint;

import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.configuration.security.ClientAuthentication;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.FindDelegationsResponderInterfaceImpl;
import se.vgregion.delegation.ws.GetActiveDelegationsResponderInterfaceImpl;
import se.vgregion.delegation.ws.GetDelegationResponderInterfaceImpl;
import se.vgregion.delegation.ws.GetDelegationsResponderInterfaceImpl;
import se.vgregion.delegation.ws.GetDelegationsbyUnitAndRoleResponderInterfaceImpl;
import se.vgregion.delegation.ws.GetInactiveDelegationsResponderInterfaceImpl;
import se.vgregion.delegation.ws.HasDelegationResponderInterfaceImpl;
import se.vgregion.delegation.ws.PingForConfigurationResponderInterfaceImpl;
import se.vgregion.delegation.ws.RemoveDelegationResponderInterfaceImpl;
import se.vgregion.delegation.ws.SaveDelegationsResponderInterfaceImpl;
import se.vgregion.delegation.ws.util.PropertiesBean;

public class Server {

    private PropertiesBean propertiesBean;
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static List<Endpoint> endpoints;
    private JettyHTTPServerEngineFactory engineFactory;
    private boolean https = false;

    static public void main(String[] args) throws Exception {

        Server server = new Server();
        server.start();

    }

    private void start() {
        String path = "classpath:/spring/conf.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(path);
        propertiesBean = (PropertiesBean) ctx.getBean("propertiesBean");

        String hostname = "localhost";

        try {
            hostname = InetAddress.getLocalHost().getHostName();
            LOGGER.info("Host namne = " + hostname);
        } catch (UnknownHostException e) {
            LOGGER.error("Host namne error ", e);
        }

        startServer(ctx, hostname, propertiesBean.getServerPort());
    }

    public void startServer(ClassPathXmlApplicationContext ctx, String hostname, String port) {

        Server.setEndpoints(new ArrayList<Endpoint>());

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException for: org.postgresql.Driver " + e.getMessage());
        }

        DelegationService delegationService = (DelegationService) ctx.getBean("delegationService");
        propertiesBean = (PropertiesBean) ctx.getBean("propertiesBean");

        // Make CXF use log4j (instead of JDK-logging), currently can't use slf4j.
        System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");

        https = (propertiesBean.getCertPass() != null && !propertiesBean.getCertPass().equals(""));

        String http = "http";

        // Setups SSL and Certificates.
        if (https) {
            try {
                http = "https";
                setupServerEngineFactory(Integer.parseInt(port));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        String address2 = http + "://" + hostname + ":" + port + "/getactivedelegations";
        String address3 = http + "://" + hostname + ":" + port + "/getdelegation";
        String address4 = http + "://" + hostname + ":" + port + "/getinactivedelegations";
        String address5 = http + "://" + hostname + ":" + port + "/getdelegationsbyunitandrole";
        String address6 = http + "://" + hostname + ":" + port + "/getdelegations";
        String address7 = http + "://" + hostname + ":" + port + "/hasdelegation";
        String address8 = http + "://" + hostname + ":" + port + "/savedelegations";
        String address9 = http + "://" + hostname + ":" + port + "/removedelegation";
        String address10 = http + "://" + hostname + ":" + port + "/finddelegations";
        String address11 = http + "://" + hostname + ":" + port + "/pingforconfiguration";

        LOGGER.info(
                "RIV TA Basic Profile v2.1 - Delegation Service , Apache CXF Producer running on Java version {}",
                System.getProperty("java.version"));
        LOGGER.info("Starting server...");

        startService(new GetActiveDelegationsResponderInterfaceImpl(delegationService), address2);
        startService(new GetDelegationResponderInterfaceImpl(delegationService), address3);
        startService(new GetInactiveDelegationsResponderInterfaceImpl(delegationService), address4);
        startService(new GetDelegationsbyUnitAndRoleResponderInterfaceImpl(delegationService), address5);
        startService(new GetDelegationsResponderInterfaceImpl(delegationService), address6);
        startService(new HasDelegationResponderInterfaceImpl(delegationService), address7);
        startService(new SaveDelegationsResponderInterfaceImpl(delegationService), address8);
        startService(new RemoveDelegationResponderInterfaceImpl(delegationService), address9);
        startService(new FindDelegationsResponderInterfaceImpl(delegationService), address10);
        startService(new PingForConfigurationResponderInterfaceImpl(delegationService), address11);

        LOGGER.info("Server ready!");
    }

    static private void startService(Object serviceImpl, String address) {
        Endpoint endpoint = Endpoint.publish(address, serviceImpl);
        Server.getEndpoints().add(endpoint);
        LOGGER.info("Service available at: " + address + "?wsdl");
    }

    public void stopServer(String port) {

        for (Endpoint endpoint : endpoints) {
            endpoint.stop();
        }
        endpoints = null;

        if (https) {
            engineFactory.destroyForPort(Integer.parseInt(port));
        }
        LOGGER.info("Server shutdown!");
    }

    /**
     * This method sets up the security.
     * 
     * @param port
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private void setupServerEngineFactory(int port) throws IOException, GeneralSecurityException {

        TLSServerParameters tlsParams = new TLSServerParameters();

        String userhome = System.getProperty("user.home");
        String certFilePath = userhome + "/.delegation-service/" + propertiesBean.getCertFileName();
        InputStream resourceAsStream = new FileInputStream(certFilePath);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try {
            keyStore.load(resourceAsStream, propertiesBean.getCertPass().toCharArray());
        } finally {
            resourceAsStream.close();
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, propertiesBean.getCertPass().toCharArray());
        tlsParams.setKeyManagers(keyManagerFactory.getKeyManagers());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        // tlsParams.setTrustManagers(trustManagerFactory.getTrustManagers());

        // FiltersType filter = new FiltersType();
        // filter.getInclude().add(".*");
        // tlsParams.setCipherSuitesFilter(filter);

        ClientAuthentication clientAuth = new ClientAuthentication();
        // clientAuth.setRequired(true);
        // clientAuth.setWant(true);
        clientAuth.setRequired(false);
        clientAuth.setWant(false);
        tlsParams.setClientAuthentication(clientAuth);

        // if (propertiesBean.isClientCertSecurityActive()) {
        // CertificateConstraintsType constraints = new CertificateConstraintsType();
        // DNConstraintsType constraintsType = new DNConstraintsType();
        // // constraintsType.setCombinator(CombinatorType.ANY);
        // System.out.println("propertiesBean.getRegularExpressionClientCert() "
        // + propertiesBean.getRegularExpressionClientCert());
        // String regularExpression = propertiesBean.getRegularExpressionClientCert();
        // // constraintsType.getRegularExpression().add(regularExpression);
        // constraints.setSubjectDNConstraints(constraintsType);
        // tlsParams.setCertConstraints(constraints);
        // }

        engineFactory = new JettyHTTPServerEngineFactory();
        engineFactory.setTLSServerParametersForPort(port, tlsParams);

    }

    public static List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public static void setEndpoints(List<Endpoint> endpoints) {
        Server.endpoints = endpoints;
    }

}
