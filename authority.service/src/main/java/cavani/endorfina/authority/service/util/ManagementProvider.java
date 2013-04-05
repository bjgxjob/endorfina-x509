package cavani.endorfina.authority.service.util;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;

import cavani.endorfina.authority.api.mngt.ManagementService;

public class ManagementProvider
{

	@EJB(lookup = "java:global/authority.core-1.0/ManagementServiceBean!cavani.endorfina.authority.api.mngt.ManagementService")
	ManagementService service;

	@Produces
	ManagementService getService()
	{
		return service;
	}

}
