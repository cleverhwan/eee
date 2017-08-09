package pjh;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Ldap {
	private static String userId = "test";
	private static String passwd = "pass";
	private static String ldapDomain = "ds.mycompany.com";
	private static String ldapPathFormat = "LDAP://%s/";

	public static void main(String[] args) {
		String path = String.format(ldapPathFormat, ldapDomain);
		System.out.println(path);

		String filterString = "(cn=mrtint)";

		// LDAP Context
		DirContext context = null;

		// LDAP 접속 환경 설정
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, path);
		properties.put(Context.SECURITY_AUTHENTICATION, "simple");
		properties.put(Context.SECURITY_PRINCIPAL, userId);
		properties.put(Context.SECURITY_CREDENTIALS, passwd);

		try {
			context = new InitialDirContext(properties);
			SearchControls searcher = new SearchControls();
			// 기본 엔트리에서 시작해서 하위까지 하는거임.
			searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> results = context.search(
					"OU=Employee,DC=ds,DC=mycompany,DC=com", filterString, searcher);
			while (results.hasMore()) {
				SearchResult result = results.next();
				Attributes attrs = result.getAttributes();
				System.out.println(attrs.get("displayName"));
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		System.out.println(isAuthenticatedUser("mrtint@ds.mycompany.com",
				"test123"));

	}

	/**
	 * LDAP 계정과 암호를 이용한 사용자 인증
	 * 
	 * @param userId
	 *            계정명
	 * @param password
	 *            암호
	 * @return 인증 여부 (ID / PASS 가 일치하는지 아닌지를 확인함)
	 */
	public static boolean isAuthenticatedUser(String userId, String password) {
		boolean isAuthenticated = false;
		String path = String.format(ldapPathFormat, ldapDomain);
		if (password != null && password != "") {
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			properties.put(Context.PROVIDER_URL, path);
			properties.put(Context.SECURITY_AUTHENTICATION, "simple");
			properties.put(Context.SECURITY_PRINCIPAL, userId);
			properties.put(Context.SECURITY_CREDENTIALS, password);
			try {
				DirContext con = new InitialDirContext(properties);
				isAuthenticated = true;
				con.close();
			} catch (NamingException e) {
			}
		}
		return isAuthenticated;
	}
}