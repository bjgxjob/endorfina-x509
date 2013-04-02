package cavani.endorfina.authority.core;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class LoggerResolver
{

	@Produces
	Logger getLogger(final InjectionPoint ip)
	{
		final String category = ip.getMember().getDeclaringClass().getName();
		return Logger.getLogger(category);
	}

}
