package cavani.endorfina.authority.api;

import java.util.List;

import javax.ejb.Local;

@Local
public interface AuthorityService
{

	String request();

	void revoke(String id);

	byte[] pkcs12(String id);

	List<CredentialData> list();

}
