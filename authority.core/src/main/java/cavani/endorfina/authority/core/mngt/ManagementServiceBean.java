package cavani.endorfina.authority.core.mngt;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import cavani.endorfina.authority.api.mngt.ManagementService;
import cavani.endorfina.authority.api.model.CredentialData;
import cavani.endorfina.authority.core.engine.CredentialIdentity;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ManagementServiceBean implements ManagementService
{

	@Inject
	Logger systemLog;

	@Inject
	AuthorityManager manager;

	@Inject
	CredentialIdentity identity;

	@Override
	public String request()
	{
		systemLog.info("request");

		final String id = identity.id();

		manager.request(id);

		return id;
	}

	@Override
	public void revoke(final String id)
	{
		try
		{
			systemLog.info("revoke: " + id);
			manager.revoke(id);
		}
		catch (final Exception e)
		{
			throw new EJBException("Error revoking credential!", e);
		}
	}

	@Override
	public byte[] pkcs12(final String id)
	{
		try
		{
			systemLog.info("pkcs12: " + id);
			return manager.p12(id);
		}
		catch (final Exception e)
		{
			throw new EJBException("Error getting credential (raw)!", e);
		}
	}

	@Override
	public List<CredentialData> credentialList()
	{
		try
		{
			systemLog.info("credentials");
			return manager.credentials();
		}
		catch (final Exception e)
		{
			throw new EJBException("Error listing credentials!", e);
		}
	}

}
