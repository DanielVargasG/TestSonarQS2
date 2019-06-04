package edn.cloud.web.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edn.cloud.sfactor.persistence.dao.ErrorLogDAO;
import edn.cloud.sfactor.persistence.entities.ErrorLog;

@Path("/json")
public class JsonErrorService {

	@GET
	@Path("/errors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response displayErrors() {
		ErrorLogDAO erroDAO = new ErrorLogDAO();
		List<ErrorLog> list = erroDAO.getAll();

		return Response.status(200).entity(list).build();
	}

	@GET
	@Path("/errors/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response displayErrorsById(@PathParam("id") Long id) {
		ErrorLogDAO erroDAO = new ErrorLogDAO();
		List<ErrorLog> list = erroDAO.getByDocId(id);

		return Response.status(200).entity(list).build();
	}

}
