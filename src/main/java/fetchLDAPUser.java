import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.Enumeration;
import org.json.JSONObject; // Ensure you have a JSON library like org.json added to your project.

public class fetchLDAPUser {
    public static String fetchLDAPUser(String ldapUrl, String username, String password, String searchBaseOU, String searchFilterKeyword, String SearchFilterValue) throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username); // Adjust DN
        env.put(Context.SECURITY_CREDENTIALS, password);
//        env.put("com.sun.jndi.ldap.trace.ber", System.out); // Uncomment for debugging

        DirContext ctx = null;
        try {
            ctx = new InitialDirContext(env);
            String searchBase =searchBaseOU;
            String searchFilter = "("+searchFilterKeyword+"=" + SearchFilterValue + ")";

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchControls);

            if (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();

                // Build JSON object for the attributes
                JSONObject json = new JSONObject();
                Enumeration<? extends Attribute> allAttrs = attrs.getAll();

                while (allAttrs.hasMoreElements()) {
                    Attribute attr = allAttrs.nextElement();
                    json.put(attr.getID(), attr.get()); // Add each attribute to the JSON object
                }

                return json.toString(); // Return JSON as string
            } else {
                return "{\"error\": \"User not found in LDAP.\"}"; // Return error as JSON
            }
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
            String userID = "pgeorge"; // Replace with a valid userID
            String jsonResult = fetchLDAPUser("ldap://10.0.0.8:389","CN=Mohamed Reda,OU=DEV,OU=Sumerge,DC=sumergedc,DC=local","MRP@$$w000rd","OU=DEV,OU=Sumerge,DC=sumergedc,DC=local","sAMAccountName","mreda");
            System.out.println("LDAP User Data: " + jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
}
}