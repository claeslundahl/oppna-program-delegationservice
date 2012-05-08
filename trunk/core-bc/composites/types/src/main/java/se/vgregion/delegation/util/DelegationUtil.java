package se.vgregion.delegation.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections.BeanMap;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationStatus;

public class DelegationUtil {

    public static class MyBeanMap extends BeanMap {

        /**
         * 
         */
        public MyBeanMap(Object bean) {
            super(bean);
        }

        /* (non-Javadoc)
         * 
         * @see org.apache.commons.collections.BeanMap#put(java.lang.Object, java.lang.Object) */
        @Override
        public Object put(Object name, Object value) throws IllegalArgumentException, ClassCastException {
            Class<?> targetType = getType(name.toString());

            if (value instanceof XMLGregorianCalendar && targetType.isAssignableFrom(Date.class)) {
                // 2006-01-27T15:49:00+01:00
                XMLGregorianCalendar xgc = (XMLGregorianCalendar) value;
                value = toDate(xgc);
            } else if (value instanceof Date && targetType.isAssignableFrom(XMLGregorianCalendar.class)) {
                GregorianCalendar dateValue = new GregorianCalendar();
                dateValue.setTime((Date) value);
                try {
                    XMLGregorianCalendar xmlValue = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                            dateValue);
                    value = xmlValue;
                } catch (DatatypeConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.put(name, value);
        }
    }

    private static Date toDate(XMLGregorianCalendar xmlCal) {
        GregorianCalendar c = xmlCal.toGregorianCalendar();
        Date fecha = c.getTime();
        return fecha;
    }

    public static <T, F> List<T> convert(Collection<F> objs, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        for (F f : objs) {
            result.add(DelegationUtil.convert(f, clazz));
        }
        return result;
    }

    public static <T, F> T convert(F obj, Class<T> clazz) {
        T result;
        try {
            result = clazz.newInstance();
            putAllWriteable(new DelegationUtil.MyBeanMap(obj), new DelegationUtil.MyBeanMap(result));
            return result;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void putAllWriteable(DelegationUtil.MyBeanMap source, DelegationUtil.MyBeanMap target) {
        HashMap<String, Object> sourceMap = new HashMap<String, Object>(source);
        sourceMap.remove("class");
        for (String key : sourceMap.keySet()) {
            Object value = null;
            try {
                value = sourceMap.get(key);
                target.put(key, value);
            } catch (Exception e) {
                System.out.println("Key '" + key + "' failed value '" + value + "'.");
            }
        }
    }

    public static Delegation toDelegation(Object obj) {
        return convert(obj, Delegation.class);
    }

    public static List<Delegation> toDelegations(List<Object> objs) {
        List<Delegation> result = new ArrayList<Delegation>();

        for (Object obj : objs) {
            result.add(toDelegation(obj));
        }

        return result;
    }

    public static void main(String[] args) {
        Delegation d = new Delegation();
        d.setDelegatedFor("df");
        d.setDelegateTo("dt");
        d.setValidTo(new Date());
        d.setId(-1l);
        d.setValidFrom(new Date());
        d.setRole("role");
        d.setStatus(DelegationStatus.ACTIVE);
        d.setDelegationKey(-1L);

        BeanMap bm = new BeanMap(d);
        Set keys = new HashSet();
        keys.addAll(bm.keySet());
        keys.remove("class");
        StringBuilder def = new StringBuilder("insert into vgr_" + Delegation.class.getSimpleName() + " ( ");
        StringBuilder val = new StringBuilder(" values (");
        for (Object o : keys) {
            Object v = bm.get(o);
            if (v != null) {
                def.append(o + ", ");
                val.append("'" + format(bm.get(o)) + "', ");
            }
        }
        def.delete(def.length() - 2, def.length());
        val.delete(val.length() - 2, val.length());

        System.out.println(def + ") " + val + ");");
    }

    private static String format(Object o) {
        if (o instanceof Date) {
            Date d = (Date) o;
            // 2011-09-01 10:00:00.0
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
            return sdf.format(d);
        }
        return o + "";
    }

}
