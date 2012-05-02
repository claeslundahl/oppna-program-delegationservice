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

import javax.xml.ws.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.SaveDelegationsResponderInterfaceImpl;

public class Server {

    static private final Logger logger = LoggerFactory.getLogger(Server.class);

    static public void main(String[] args) throws Exception {

        // String path = "classpath:jpa-delegation-service-configuration.xml";
        // String path = "classpath:/spring/delegation-service-config.xml"

        Class.forName("org.postgresql.Driver");

        String path = "classpath:/spring/conf.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(path);
        DelegationService delegationService = (DelegationService) ctx.getBean("delegationService");

        // Make CXF use log4j (instead of JDK-logging), currently can't use slf4j
        System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");

        // Parse main arguments
        String address = null;
        if (args.length == 0) {
            // Start default service
            address = "http://localhost:24003/delegationservice";
        } else {
            // Start a service on the provided URL, note that this requires pre-configuration in cxf.xml to work,
            // i.e.: <httpj:engine port="11000">
            address = args[0];
        }

        logger.info("RIV TA Basic Profile v2.1 - Ref App, Apache CXF Producer running on Java version {}",
                System.getProperty("java.version"));
        logger.info("Starting server...");
        startService(new SaveDelegationsResponderInterfaceImpl(delegationService), address);
        logger.info("Server ready!");
    }

    static private void startService(Object serviceImpl, String address) {
        Endpoint.publish(address, serviceImpl);
        logger.info("Service available at: " + address + "?wsdl");
    }
}
