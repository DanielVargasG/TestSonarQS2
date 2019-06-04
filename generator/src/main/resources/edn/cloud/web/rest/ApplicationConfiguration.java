package edn.cloud.web.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import edn.cloud.web.rest.JsonAdminService;
import edn.cloud.web.rest.JsonDocumentService;
import edn.cloud.web.rest.JsonErrorService;
import edn.cloud.web.rest.JsonGeneratedService;
import edn.cloud.web.rest.JsonRestService;
import edn.cloud.web.rest.JsonTemplateService;
import edn.cloud.web.rest.JsonUserService;
import edn.cloud.web.rest.Metadata;

public class ApplicationConfiguration extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public ApplicationConfiguration(){
	     singletons.add(new Metadata());
	     singletons.add(new JsonTemplateService());
	     singletons.add(new JsonGeneratedService());
	     singletons.add(new JsonErrorService());
	     singletons.add(new JsonUserService());
	     singletons.add(new JsonDocumentService());
	     singletons.add(new JsonAdminService());
	     singletons.add(new JsonRestService());
	     singletons.add(new JsonRecruitingService());
	     singletons.add(new JsonAdmJobService());
	     singletons.add(new JsonSignatureService());
	     singletons.add(new JsonMassiveLoad());
	}
	
	@Override
	public Set<Class<?>> getClasses() {
	     return empty;
	}
	@Override
	public Set<Object> getSingletons() {
	     return singletons;
	}
}