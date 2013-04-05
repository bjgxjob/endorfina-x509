package cavani.endorfina.authority.core.data;

import java.security.cert.X509Certificate;
import java.util.List;

import cavani.endorfina.authority.api.model.CredentialData;

public interface CredentialStore
{

	String persist(final String id, final byte[] p12, final byte[] cert, final char[] pw) throws Exception;

	String remove(final String id) throws Exception;

	List<CredentialData> list() throws Exception;

	X509Certificate getCertificate(final String id) throws Exception;

	byte[] getP12Raw(final String id) throws Exception;

}
