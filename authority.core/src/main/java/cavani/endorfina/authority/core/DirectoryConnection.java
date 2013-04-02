package cavani.endorfina.authority.core;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

public class DirectoryConnection implements AutoCloseable
{

	Hashtable<String, String> env;

	DirContext context;

	public DirectoryConnection(final String url, final String binddn, final String bindpw)
	{
		env = new Hashtable<>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, binddn);
		env.put(Context.SECURITY_CREDENTIALS, bindpw);

		env.put("java.naming.ldap.attributes.binary", "userPKCS12");
	}

	public void open() throws NamingException
	{
		context = new InitialDirContext(env);
	}

	@Override
	public void close() throws NamingException
	{
		if (env != null)
		{
			env.clear();
			env = null;
		}

		if (context != null)
		{
			context.close();
			context = null;
		}
	}

	public boolean isConnected()
	{
		return context != null;
	}

	public void persist(final String dn, final DirectoryEntry entry) throws NamingException
	{
		context.bind(dn, entry);
	}

	public void remove(final String dn) throws NamingException
	{
		context.destroySubcontext(dn);
	}

	public Object query(final DirectoryQuery query) throws NamingException
	{
		final Attributes attrs = context.getAttributes(query.dn, query.attrs);

		final NamingEnumeration<?> values = attrs.getAll();
		try
		{
			while (values.hasMore())
			{
				final Attribute attribute = (Attribute) values.next();
				final String id = attribute.getID();
				final Object value = attribute.get();
				if (!query.eval(id, value))
				{
					break;
				}
			}
		}
		finally
		{
			values.close();
		}

		return query.result();
	}

	public List<Map<String, Object>> search(final String dn, final String[] attrs) throws NamingException
	{
		final List<Map<String, Object>> list = new LinkedList<>();

		final NamingEnumeration<SearchResult> result = context.search(dn, null, attrs);
		try
		{
			while (result.hasMore())
			{
				final SearchResult _result = result.next();

				final Map<String, Object> data = new LinkedHashMap<>();

				final NamingEnumeration<?> values = _result.getAttributes().getAll();
				try
				{
					while (values.hasMore())
					{
						final Attribute attribute = (Attribute) values.next();
						final String id = attribute.getID();
						final Object value = attribute.get();
						data.put(id, value);
					}
				}
				finally
				{
					values.close();
				}

				list.add(Collections.unmodifiableMap(data));
			}
		}
		finally
		{
			result.close();
		}

		return Collections.unmodifiableList(list);
	}
	// ...

}
