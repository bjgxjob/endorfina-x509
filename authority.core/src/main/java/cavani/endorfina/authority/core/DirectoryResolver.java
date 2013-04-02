package cavani.endorfina.authority.core;

import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class DirectoryResolver
{

	protected InputStream resource(final String resource)
	{
		return this.getClass().getClassLoader().getResourceAsStream(resource);
	}

	@Produces
	public DirectoryConnection connection() throws Exception
	{
		final DirectoryConnection cx;

		try (
			InputStream in = resource("META-INF/directory.properties"))
		{
			final Properties properties = new Properties();
			properties.load(in);

			final String url = properties.getProperty("url");
			final String binddn = properties.getProperty("binddn");
			final String bindpw = properties.getProperty("bindpw");

			cx = new DirectoryConnection(url, binddn, bindpw);
		}

		cx.open();

		return cx;
	}

	public void close(@Disposes final DirectoryConnection connection) throws Exception
	{
		connection.close();
	}

}
