package cavani.endorfina.authority.api.mngt;

import java.util.List;

import javax.ejb.Local;

import cavani.endorfina.authority.api.model.CredentialData;

@Local
public interface ManagementService
{

	String request();

	void revoke(String id);

	byte[] pkcs12(String id);

	CredentialData credentialData(String id);

	List<CredentialData> credentialList();

}
