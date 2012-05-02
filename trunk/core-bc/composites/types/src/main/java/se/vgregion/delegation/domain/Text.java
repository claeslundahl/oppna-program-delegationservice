package se.vgregion.delegation.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.BeanMap;

public class Text {

    public static Map<Class, String> cls2name = new HashMap<Class, String>();
    static {
        cls2name.put(Integer.class, "xs:integer");
        cls2name.put(Integer.TYPE, "xs:int");
        cls2name.put(String.class, "xs:string");
        cls2name.put(Date.class, "xs:date");
        cls2name.put(Long.class, "xs:long");
        cls2name.put(Long.TYPE, "xs:long");
    }

    public static String mkTypeName(Class<?> clazz) {
        if (cls2name.containsKey(clazz)) {
            return cls2name.get(clazz);
        }
        return "core:" + clazz.getSimpleName();
    }

    public static String mkSchemaType(Object db) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xs:complexType name='XXX'>\n".replace("XXX", db.getClass().getSimpleName()));
        sb.append("<xs:sequence>\n");

        // DelegationBlock db = new DelegationBlock();

        BeanMap bm = new BeanMap(db);
        Set keys = new HashSet();
        keys.addAll(bm.keySet());
        keys.remove("class");
        for (Object o : keys) {
            String row = "<xs:element name='XXX' type='YYY' />\n".replace("XXX", o.toString());
            row = row.replace("YYY", mkTypeName(bm.getType(o.toString())));
            sb.append(row);
        }

        sb.append("</xs:sequence>\n");
        sb.append("</xs:complexType>\n");

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(mkSchemaType(new Delegation()));

        System.out.println(mkSchemaType(new DelegationBlock()));
    }

}
