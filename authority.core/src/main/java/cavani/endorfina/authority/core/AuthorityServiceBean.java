package cavani.endorfina.authority.core;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import cavani.endorfina.authority.api.AuthorityService;
import cavani.endorfina.authority.api.CredentialData;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AuthorityServiceBean implements AuthorityService
{

	@Inject
	Logger systemLog;

	@Inject
	Authority authority;

	@Inject
	CredentialIdentity identity;

	@Override
	public String request()
	{
		systemLog.info("request");

		final String id = identity.id();

		authority.request(id);

		return id;
	}

	@Override
	public void revoke(final String id)
	{
		try
		{
			systemLog.info("revoke: " + id);
			authority.revoke(id);
		}
		catch (final Exception e)
		{
			throw new EJBException("Erro revogando credencial!", e);
		}
	}

	@Override
	public byte[] pkcs12(final String id)
	{
		try
		{
			systemLog.info("pkcs12: " + id);
			return authority.raw(id);
		}
		catch (final Exception e)
		{
			throw new EJBException("Erro obtendo credencial (bruta)!", e);
		}
	}

	@Override
	public List<CredentialData> list()
	{
		try
		{
			systemLog.info("list");
			return authority.list();
		}
		catch (final Exception e)
		{
			throw new EJBException("Erro listando credenciais!", e);
		}
	}

}
