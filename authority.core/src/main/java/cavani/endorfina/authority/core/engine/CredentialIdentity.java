package cavani.endorfina.authority.core.engine;

import java.util.UUID;

import javax.ejb.Singleton;

@Singleton
public class CredentialIdentity
{

	public String id()
	{
		return UUID.randomUUID().toString().replaceAll("[^\\w]", "");
	}

}
