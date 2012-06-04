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

}
