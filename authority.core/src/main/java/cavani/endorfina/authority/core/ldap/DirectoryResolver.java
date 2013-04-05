package cavani.endorfina.authority.core.ldap;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class DirectoryResolver
{

	@Inject
	DirectoryConfiguration config;

	@Produces
	public DirectoryConnection connection() throws Exception
	{
		final String url = config.getUrl();
		final String binddn = config.getBinddn();
		final String bindpw = config.getBindpw();
		final DirectoryConnection cx = new DirectoryConnection(url, binddn, bindpw);

		cx.open();

		return cx;
	}

	public void close(@Disposes final DirectoryConnection connection) throws Exception
	{
		connection.close();
	}

}
