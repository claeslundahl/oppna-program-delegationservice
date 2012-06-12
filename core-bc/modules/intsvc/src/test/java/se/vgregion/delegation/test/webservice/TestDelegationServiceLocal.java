/**
 * 
 */
package se.vgregion.delegation.test.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class TestDelegationServiceLocal extends TestDelegationServiceWS {
    static private final Logger logger = LoggerFactory.getLogger(TestDelegationServiceLocal.class);

    @Override
    protected void setUpContext() {
        String path = "classpath:/settings/serverConfLocal.xml";
        context = new ClassPathXmlApplicationContext(path);
    }
}
