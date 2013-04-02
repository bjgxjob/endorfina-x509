package cavani.endorfina.authority.api;

import java.security.cert.X509Certificate;

import javax.ejb.Local;

@Local
public interface AuthenticationService
{

	boolean validateCredential(String id, X509Certificate certificate);

}
