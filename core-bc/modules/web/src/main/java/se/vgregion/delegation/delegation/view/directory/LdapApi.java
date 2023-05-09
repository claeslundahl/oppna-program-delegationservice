package se.vgregion.delegation.delegation.view.directory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LdapApi extends LdapInf {

    private String initialContextFactoryClassName = "com.sun.jndi.ldap.LdapCtxFactory";

    private int resultLimit = 500;

    private String[] attributeFilter = {"*"};

    public LdapApi() {
        super();
    }

    public LdapApi(LdapInf ldapInf) {
        this();
        this.url = ldapInf.getUrl();
        this.user = ldapInf.getUser();
        this.password = ldapInf.getPassword();
        this.base = ldapInf.getBase();
        this.userDnTemplate = ldapInf.getUserDnTemplate();
    }

    public List<Map<String, Object>> query(String filter) {
        try {
            return queryImp(filter);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Map<String, Object>> queryImp(String filter) throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryClassName);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, password);

        env.put("java.naming.ldap.factory.socket", TrustStoreSslSocketFactory.class.getName());

        DirContext context = new InitialDirContext(env);

        SearchControls sc = new SearchControls();

        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration results = context.search(base, filter, sc);
        List<Map<String, Object>> result = toMaps(results);
        context.close();
        return result;
    }

    public List<Map<String, Object>> toMaps(NamingEnumeration results) {
        try {
            return toMapsImpl(results, resultLimit);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> toMaps(NamingEnumeration results, Integer resultLimit) {
        try {
            return toMapsImpl(results, resultLimit);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Map<String, Object>> toMapsImpl(NamingEnumeration results, Integer resultLimit) throws NamingException {
        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        while (results.hasMore()) {
            SearchResult sr = (SearchResult) results.next();
            // System.out.println("sr.getNameInNamespace() " + sr.getNameInNamespace());
            Map<String, Object> map = toMap(sr.getAttributes());
            result.add(map);
            if (map.containsKey("dn")) throw new RuntimeException();
            map.put("dn", sr.getNameInNamespace());
            count++;
            if (resultLimit != null && count > resultLimit)
                break;
        }
        return result;
    }

    public static Map<String, Object> toMap(Attributes attributes) {
        try {
            return toMapImp(attributes);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    static private Map<String, Object> toMapImp(Attributes attributes) throws NamingException {
        Map<String, Object> result = new TreeMap<>();
        NamingEnumeration<? extends Attribute> all = attributes.getAll();

        while (all.hasMore()) {
            Attribute item = all.next();
            if (item.size() == 1) {
                result.put(item.getID(), handleDifferentKind(item.get()));
            } else {
                List results = new ArrayList();
                for (int i = 0; i < item.size(); i++) results.add(handleDifferentKind(item.get(i)));
                result.put(item.getID(), results);
            }
        }
        return result;
    }

    static Object handleDifferentKind(Object ofValue) {
        /*if (ofValue != null && ofValue.getClass().isArray()) {
            return toList(ofValue);
        }*/
        return ofValue;
    }

    public String getInitialContextFactoryClassName() {
        return initialContextFactoryClassName;
    }

    public void setInitialContextFactoryClassName(String initialContextFactoryClassName) {
        this.initialContextFactoryClassName = initialContextFactoryClassName;
    }

    public int getResultLimit() {
        return resultLimit;
    }

    public void setResultLimit(int resultLimit) {
        this.resultLimit = resultLimit;
    }

    public String[] getAttributeFilter() {
        return attributeFilter;
    }

    public void setAttributeFilter(String[] attributeFilter) {
        this.attributeFilter = attributeFilter;
    }

    public void verifyPasswordWithException(String distinguishedName, String password) throws NamingException {
        Properties authEnv = new Properties();
        authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        authEnv.put(Context.PROVIDER_URL, url);
        authEnv.put(Context.SECURITY_PRINCIPAL, distinguishedName);
        authEnv.put(Context.SECURITY_CREDENTIALS, password);

        authEnv.put("java.naming.ldap.factory.socket", TrustStoreSslSocketFactory.class.getName());

        new InitialDirContext(authEnv);
    }

    public boolean verifyPassword(String distinguishedName, String password) {
        try {
            verifyPasswordWithException(distinguishedName, password);
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    public static LdapApi getDefaultInstance() {
        Properties prop = getApplicationProperties();
        if (prop == null) return null;
        LdapInf li = new LdapInf();
        li.setUser(prop.getProperty("ldap.userDn"));
        li.setUrl(prop.getProperty("ldap.url"));
        li.setPassword(prop.getProperty("ldap.password"));
        li.setBase(prop.getProperty("ldap.base"));
        LdapApi ldap = new LdapApi(li);
        return ldap;
    }

    static Properties getApplicationProperties() {
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".app", "regionkalendern", "application.properties");
            if (!Files.exists(path))
                return null;
            Properties properties = new Properties();
            properties.load(new FileInputStream(path.toFile()));
            return properties;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
