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

}
