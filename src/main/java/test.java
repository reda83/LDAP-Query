import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.Enumeration;

import org.json.JSONArray;
import org.json.JSONObject;

public class test {
    public static String test(String ldapUrl, String username, String password, String searchBaseOU, String searchFilterKeyword, String SearchFilterValue) throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        DirContext ctx = null;
        try {
            ctx = new InitialDirContext(env);
            String searchBase = searchBaseOU;
            String searchFilter = "(" + searchFilterKeyword + "=" + SearchFilterValue + ")";

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchControls);
            JSONArray entries = new JSONArray();

            while (results.hasMore()) {
                SearchResult sr = results.next();
                JSONObject entry = new JSONObject();
                entry.put("dn", sr.getNameInNamespace());
                Attributes attrs = sr.getAttributes();
                JSONObject attributes = new JSONObject();
                NamingEnumeration<? extends Attribute> allAttrs = attrs.getAll();
                while (allAttrs.hasMore()) {
                    Attribute attr = allAttrs.next();
                    JSONArray values = new JSONArray();
                    NamingEnumeration<?> vals = attr.getAll();
                    while (vals.hasMore()) {
                        values.put(vals.next());
                    }
                    attributes.put(attr.getID(), values);
                }
                entry.put("attributes", attributes);
                entries.put(entry);
            }

            return entries.toString();
        } catch (AuthenticationException ae) {
            return "{\"error\": \"Authentication failed: " + ae.getMessage() + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Error occurred: " + e.getMessage() + "\"}";
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public static void main(String[] args) {
        fetchLDAPUser ldapFetcher = new fetchLDAPUser();
        try {
            String jsonResult = test("ldap://10.0.0.8:389", "CN=Mohamed Reda,OU=DEV,OU=Sumerge,DC=sumergedc,DC=local", "mypassword", "OU=DEV,OU=Sumerge,DC=sumergedc,DC=local", "sAMAccountName", "mreda");
            System.out.println("LDAP Search Result Count: " + jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
