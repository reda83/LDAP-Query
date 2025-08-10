import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.Enumeration;
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
            int resultCount = 0; // Initialize the count variable

            while (results.hasMore()) {
                results.next(); // Move to the next result
                resultCount++; // Increment the count for each result
            }

            return "{\"resultCount\": " + resultCount + "}"; // Return the count as a JSON object
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
            String jsonResult = test("ldap://10.0.0.8:389", "CN=Mohamed Reda,OU=DEV,OU=Sumerge,DC=sumergedc,DC=local", "mypasswordhere", "OU=DEV,OU=Sumerge,DC=sumergedc,DC=local", "sAMAccountName", "mreda");
            System.out.println("LDAP Search Result Count: " + jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
