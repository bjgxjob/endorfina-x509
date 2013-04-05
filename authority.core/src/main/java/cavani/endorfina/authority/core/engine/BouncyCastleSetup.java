package cavani.endorfina.authority.core.engine;

import java.security.Provider;
import java.security.Security;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Singleton
@Startup
public class BouncyCastleSetup
{

	@Inject
	Logger systemLog;

	@PostConstruct
	public void setup()
	{
		systemLog.info("BouncyCastle setup...");

		if (Security.getProvider("BC") == null)
		{
			final Provider provider = new BouncyCastleProvider();
			Security.addProvider(provider);
		}
	}

}
