package cavani.endorfina.authority.core.data;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public abstract class DirectoryEntry implements DirContext
{

	protected Attributes attributes = new BasicAttributes(true);

	protected Attribute objectClasses = new BasicAttribute("objectClass");

	protected Attribute ous = new BasicAttribute("ou");

	@Override
	public Attributes getAttributes(final String name) throws NamingException
	{
		if (!name.isEmpty())
		{
			throw new NameNotFoundException();
		}

		return attributes;
	}

	@Override
	public Attributes getAttributes(final Name name) throws NamingException
	{
		return getAttributes(name.toString());
	}

	@Override
	public Attributes getAttributes(final String name, final String[] ids) throws NamingException
	{
		if (!name.isEmpty())
		{
			throw new NameNotFoundException();
		}

		final Attributes answer = new BasicAttributes(true);

		Attribute target = null;

		for (final String id : ids)
		{
			target = attributes.get(id);
			if (target != null)
			{
				answer.put(target);
			}
		}

		return answer;
	}

	@Override
	public Attributes getAttributes(final Name name, final String[] ids) throws NamingException
	{
		return getAttributes(name.toString(), ids);
	}

	@Override
	public void bind(final Name name, final Object obj, final Attributes attrs) throws NamingException
	{
	}

	@Override
	public void bind(final String name, final Object obj, final Attributes attrs) throws NamingException
	{
	}

	@Override
	public DirContext createSubcontext(final Name name, final Attributes attrs) throws NamingException
	{
		return null;
	}

	@Override
	public DirContext createSubcontext(final String name, final Attributes attrs) throws NamingException
	{
		return null;
	}

	@Override
	public DirContext getSchema(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public DirContext getSchema(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public DirContext getSchemaClassDefinition(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public DirContext getSchemaClassDefinition(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public void modifyAttributes(final Name name, final ModificationItem[] mods) throws NamingException
	{
	}

	@Override
	public void modifyAttributes(final String name, final ModificationItem[] mods) throws NamingException
	{
	}

	@Override
	public void modifyAttributes(final Name name, final int mod_op, final Attributes attrs) throws NamingException
	{
	}

	@Override
	public void modifyAttributes(final String name, final int mod_op, final Attributes attrs) throws NamingException
	{
	}

	@Override
	public void rebind(final Name name, final Object obj, final Attributes attrs) throws NamingException
	{
	}

	@Override
	public void rebind(final String name, final Object obj, final Attributes attrs) throws NamingException
	{
	}

	@Override
	public NamingEnumeration<SearchResult> search(final Name name, final Attributes matchingAttributes) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(final String name, final Attributes matchingAttributes) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(final Name name, final Attributes matchingAttributes, final String[] attributesToReturn) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(final String name, final Attributes matchingAttributes, final String[] attributesToReturn) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(final Name name, final String filter, final SearchControls cons) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(final String name, final String filter, final SearchControls cons) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(final Name name, final String filterExpr, final Object[] filterArgs, final SearchControls cons) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(final String name, final String filterExpr, final Object[] filterArgs, final SearchControls cons) throws NamingException
	{
		return null;
	}

	@Override
	public Object addToEnvironment(final String propName, final Object propVal) throws NamingException
	{
		return null;
	}

	@Override
	public void bind(final Name name, final Object obj) throws NamingException
	{
	}

	@Override
	public void bind(final String name, final Object obj) throws NamingException
	{
	}

	@Override
	public void close() throws NamingException
	{
	}

	@Override
	public Name composeName(final Name name, final Name prefix) throws NamingException
	{
		return null;
	}

	@Override
	public String composeName(final String name, final String prefix) throws NamingException
	{
		return null;
	}

	@Override
	public Context createSubcontext(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public Context createSubcontext(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public void destroySubcontext(final Name name) throws NamingException
	{
	}

	@Override
	public void destroySubcontext(final String name) throws NamingException
	{
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException
	{
		return null;
	}

	@Override
	public String getNameInNamespace() throws NamingException
	{
		return null;
	}

	@Override
	public NameParser getNameParser(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public NameParser getNameParser(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public Object lookup(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public Object lookup(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public Object lookupLink(final Name name) throws NamingException
	{
		return null;
	}

	@Override
	public Object lookupLink(final String name) throws NamingException
	{
		return null;
	}

	@Override
	public void rebind(final Name name, final Object obj) throws NamingException
	{
	}

	@Override
	public void rebind(final String name, final Object obj) throws NamingException
	{
	}

	@Override
	public Object removeFromEnvironment(final String propName) throws NamingException
	{
		return null;
	}

	@Override
	public void rename(final Name oldName, final Name newName) throws NamingException
	{
	}

	@Override
	public void rename(final String oldName, final String newName) throws NamingException
	{
	}

	@Override
	public void unbind(final Name name) throws NamingException
	{
	}

	@Override
	public void unbind(final String name) throws NamingException
	{
	}

}
