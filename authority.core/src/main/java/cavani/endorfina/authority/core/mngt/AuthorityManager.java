package cavani.endorfina.authority.core.mngt;

import java.util.List;

import javax.inject.Inject;

import cavani.endorfina.authority.api.model.CredentialData;
import cavani.endorfina.authority.core.data.CredentialStore;
import cavani.endorfina.authority.core.engine.AuthorityEngine;

public class AuthorityManager
{

	@Inject
	AuthorityEngine engine;

	@Inject
	CredentialStore store;

	public void request(final String id)
	{
		engine.request(id);
	}

	public void revoke(final String id) throws Exception
	{
		store.remove(id);
	}

	public byte[] p12(final String id) throws Exception
	{
		return store.getP12Raw(id);
	}

	public List<CredentialData> credentials() throws Exception
	{
		return store.list();
	}
}
