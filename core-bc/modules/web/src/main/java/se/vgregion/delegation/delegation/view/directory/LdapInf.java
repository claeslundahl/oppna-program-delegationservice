package se.vgregion.delegation.delegation.view.directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LdapInf {

    protected String url, user, password, base, userDnTemplate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUserDnTemplate() {
        return userDnTemplate;
    }

    public void setUserDnTemplate(String userDnTemplate) {
        this.userDnTemplate = userDnTemplate;
    }


    public static class Entry extends HashMap<String, Object> {

        private final List<Entry> entries = new ArrayList<>();

        public List<Entry> getEntries() {
            return entries;
        }

    }


}
