package cavani.endorfina.authority.service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import cavani.endorfina.authority.api.mngt.ManagementService;
import cavani.endorfina.authority.api.model.CredentialData;

@Path("/idm")
public class Management
{

	@Inject
	Logger systemLog;

	@Inject
	ManagementService service;

	@GET
	@Path("/request")
	public String create()
	{
		systemLog.info("Invoking authority/idm/request");
		return service.request();
	}

	@GET
	@Path("/revoke")
	public String revoke(@QueryParam("id") final String id)
	{
		systemLog.info("Invoking authority/idm/revoke?id=" + id);
		service.revoke(id);
		return id;
	}

	@GET
	@Path("/data")
	public String data(@QueryParam("id") final String id)
	{
		systemLog.info("Invoking authority/idm/data?id=" + id);

		final CredentialData data = service.credentialData(id);
		if (data == null)
		{
			return "null";
		}

		final StringBuilder out = new StringBuilder();
		out.append("Identity: ").append(data.getId()).append("\n");
		out.append("Password: ").append(data.getPw()).append("\n");
		out.append("Created: ").append(data.getCreated()).append("\n");
		return out.toString();
	}

	@GET
	@Path("/pkcs12")
	@Produces("application/octet-stream")
	public StreamingOutput pkcs12(@QueryParam("id") final String id)
	{
		systemLog.info("Invoking authority/idm/pkcs12?id=" + id);
		final byte[] raw = service.pkcs12(id);
		return new StreamingOutput()
		{

			@Override
			public void write(final java.io.OutputStream output) throws IOException, WebApplicationException
			{
				output.write(raw);
			}

		};
	}

	@GET
	@Path("/list")
	public String list()
	{
		systemLog.info("Invoking authority/idm/list");

		final List<CredentialData> list = service.credentialList();
		if (list == null)
		{
			return "null";
		}
		if (list.isEmpty())
		{
			return "empty";
		}

		final StringBuilder out = new StringBuilder();

		for (final CredentialData data : list)
		{
			out.append("Identity: ").append(data.getId()).append(", ");
			out.append("Password: ").append(data.getPw()).append(", ");
			out.append("Created: ").append(data.getCreated()).append("\n");
		}

		return out.toString();
	}

}
