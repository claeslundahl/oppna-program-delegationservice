package se.vgregion.delegation.delegation.view.beans;

import lombok.Data;
import se.vgregion.delegation.delegation.view.domain.Delegation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Date;

@Data
public class DelegationDto {

    private String delegateTo;
    //private String delegatedFor;
    private String delegatedForEmail;
    //private java.lang.Long delegationKey;
    //private java.lang.Integer expiryAlertSentCount;
    private java.lang.Long id;
    private String information;
    //private String role;
    private Date validFrom;
    private Date validTo;

    public DelegationDto(Delegation copyFrom) {
        this();
        setDelegateTo(copyFrom.getDelegateTo());
        //setDelegatedFor(copyFrom.getDelegatedFor());
        setDelegatedForEmail(copyFrom.getDelegatedForEmail());
        /*setDelegationKey(copyFrom.getDelegationKey());
        setExpiryAlertSentCount(copyFrom.getExpiryAlertSentCount());*/
        setId(copyFrom.getId());
        setInformation(copyFrom.getInformation());
        //setRole(copyFrom.getRole());
        setValidFrom(copyFrom.getValidFrom());
        setValidTo(copyFrom.getValidTo());
    }

    public DelegationDto() {
        super();
    }

    public static void main(String[] args) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(Delegation.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : propertyDescriptors) {
            if (pd.getPropertyType().getName().startsWith("java") && pd.getWriteMethod() != null) {
                // System.out.println("private " + propertyDescriptor.getPropertyType().getName() + " " + propertyDescriptor.getName() + ";");
                System.out.println(pd.getWriteMethod().getName() + "(copyFrom." + pd.getReadMethod().getName() + "());");
            }
        }
    }

}
