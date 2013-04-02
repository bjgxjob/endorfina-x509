package cavani.endorfina.authority.adapter;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiConnection implements AutoCloseable
{

	final InitialContext context;

	public JndiConnection() throws NamingException
	{
		context = new InitialContext();
	}

	public <T> T lookup(final Class<T> type, final String path) throws NamingException
	{
		return type.cast(context.lookup(path));
	}

	@Override
	public void close() throws Exception
	{
		context.close();
	}

}
