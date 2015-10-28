package se.vgregion.delegation.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;

public class DelegationUtil {

    static private final Logger logger = LoggerFactory.getLogger(DelegationUtil.class);

    public static class MyBeanMap extends BeanMap {

        /**
         *
         */
        public MyBeanMap(Object bean) {
            super(bean);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.commons.collections.BeanMap#put(java.lang.Object, java.lang.Object)
         */
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
                    XMLGregorianCalendar xmlValue =
                            DatatypeFactory.newInstance().newXMLGregorianCalendar(dateValue);
                    value = xmlValue;
                } catch (DatatypeConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.put(name, value);
        }

        public boolean haveAnyAnnotation(String key, Class<? extends Annotation>... ofThese) {
            List<Annotation> annotations = getFieldAnnotations(key);
            //List<Annotation> ofThseAsList = Arrays.asList(ofThese);
            for (Annotation onBeanField : annotations) {
                for (Class<? extends Annotation> lookFor : ofThese) {
                    if (lookFor.isAssignableFrom(onBeanField.getClass())) return true;
                }
            }
            return false;
        }

        public List<Annotation> getFieldAnnotations(String key) {
            return getFieldAnnotationsImpl(getBean().getClass(), key);
        }

        protected List<Annotation> getFieldAnnotationsImpl(Class clazz, String key) {
            try {
                Field field = clazz.getDeclaredField(key);
                return Arrays.asList(field.getAnnotations());
            } catch (NoSuchFieldException e) {
                return Collections.emptyList();
            }
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
        @SuppressWarnings("unchecked")
        HashMap<String, Object> sourceMap = new HashMap<String, Object>(source);
        sourceMap.remove("class");
        for (Entry<String, Object> set : sourceMap.entrySet()) {
            try {
                target.put(set.getKey(), set.getValue());
            } catch (Exception e) {
                logger.debug("Key '" + set.getKey() + "' failed value '" + set.getValue() + "'.");
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

    public static DelegationBlock toDelegationBlock(Object obj) {
        DelegationUtil.MyBeanMap bm = new DelegationUtil.MyBeanMap(obj);
        DelegationBlock result = DelegationUtil.convert(obj, DelegationBlock.class);

        String d = "delegations";
        if (bm.containsKey(d)) {
            Object values = bm.get(d);
            if (values instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) values;
                for (Object o : collection) {
                    result.addDelegation(DelegationUtil.toDelegation(o));
                }
            }
        }

        return result;
    }


}
