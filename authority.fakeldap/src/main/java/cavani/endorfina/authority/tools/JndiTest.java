package cavani.endorfina.authority.tools;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class JndiTest
{

	private JndiTest()
	{
	}

	public static void main(final String[] args) throws Exception
	{
		final Hashtable<String, String> env = new Hashtable<>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://127.0.0.1:38900");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=authority,dc=endorfina,dc=com");
		env.put(Context.SECURITY_CREDENTIALS, "secret");

		env.put("java.naming.ldap.attributes.binary", "userPKCS12");

		final DirContext context = new InitialDirContext(env);

		try
		{
			final String dn = "ou=Credentials,o=Authority,dc=endorfina,dc=com";
			final NamingEnumeration<NameClassPair> values = context.list(dn);
			try
			{
				while (values.hasMore())
				{
					final NameClassPair value = values.next();
					System.out.println(value.getName() + ", " + value.getClassName());
				}
			}
			finally
			{
				values.close();
			}
		}
		finally
		{
			context.close();
		}
	}

}
