package edn.cloud.web.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilGsonFactory;
import edn.cloud.business.dto.ResultBuilderDto;
import edn.cloud.business.dto.integration.SFAdmin;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeCtrInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeInfoDto;
import edn.cloud.business.dto.ppd.api.PpdCoreEmployeeOrgInfoDto;
import edn.cloud.business.scheduler.EventListenerRolesJob;
import edn.cloud.sfactor.business.facade.SuccesFactorUserFacade;
import edn.cloud.sfactor.business.facade.SuccessFactorAdminFacade;
import edn.cloud.sfactor.business.utils.QueryBuilder;
import edn.cloud.sfactor.business.utils.StructureBuilder;
import edn.cloud.sfactor.persistence.dao.SFAdminDAO;
import edn.cloud.sfactor.persistence.entities.AdminParameters;

@Path("/json")
public class Metadata {

	private SuccessFactorAdminFacade successFactorAdmin = new SuccessFactorAdminFacade();
	// private SuccessFactorOnboardingImpl SFOnboarding = new
	// SuccessFactorOnboardingImpl();

	@GET
	@Path("/$metadata")
	@Produces(MediaType.APPLICATION_XML)
	public Response getMeta() {

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("metadata", "data");
		String meta = "";
		meta += "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>";
		meta += "<edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\">";
		meta += "<edmx:DataServices m:DataServiceVersion=\"1.0\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">";
		meta += "<Schema Namespace=\"GA\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns=\"http://schemas.microsoft.com/ado/2007/05/edm\">";
		meta += "<EntityType Name=\"Query1\"><Key><PropertyRef Name=\"Medium\" />";
		meta += "</Key>";
		meta += "<Property Name=\"Arago\" Type=\"Edm.String\" Nullable=\"false\" />";
		meta += "<Property Name=\"PPdoc Advanced\" Type=\"Edm.Int32\" />";
		meta += "</EntityType>";
		// meta += "<EntitySet Name=\"Query1\" EntityType=\"GA.Query1\" />";
		// meta += "</EntityContainer>";
		meta += "</Schema>";
		meta += "</edmx:DataServices>";
		meta += "</edmx:Edmx>";
		return Response.status(200).entity(meta).build();
	}

	@GET
	@Path("/structureFull")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppStruc() {
		String concReturn = "STRUCTURE_USERID";
		AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
		if (paramAdmUserid != null) {
			concReturn = paramAdmUserid.getValue();
		}
		ArrayList<PpdCoreEmployeeCtrInfoDto> obj = StructureBuilder.getInstance().getFullOrga("ehernandez", "companyNav/country|territoryCode,territoryName|iso2", "2018-12-29", true, concReturn);

		return Response.status(200).entity(obj).build();
	}

	@GET
	@Path("/structureOld")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppStrucOld() {
		String concReturn = "STRUCTURE_USERID";
		AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
		if (paramAdmUserid != null) {
			concReturn = paramAdmUserid.getValue();
		}
		ArrayList<PpdCoreEmployeeOrgInfoDto> obj = StructureBuilder.getInstance().getStructure("ehernandez", "companyNav/country|territoryCode,territoryName|iso2", "2018-12-29", true, concReturn);

		return Response.status(200).entity(obj).build();
	}

	@GET
	@Path("/structureCountry")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppStrucCo() {
		String concReturn = "USERID";
		AdminParameters paramAdmUserid = successFactorAdmin.adminParamGetByNameCode(UtilCodesEnum.CODE_PARAM_REFERENCEID_USERID.getCode());
		if (paramAdmUserid != null) {
			concReturn = paramAdmUserid.getValue();
		}
		ArrayList<PpdCoreEmployeeCtrInfoDto> obj = StructureBuilder.getInstance().getCountry("10052671", "department|externalCode,name|ITERATIVE|parent", "2019-01-29", false, concReturn);

		return Response.status(200).entity(obj).build();
	}

	@GET
	@Path("/mappingTest")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppMap() {

		Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();

		/*
		 * map.put("lastname", new
		 * ResultBuilderDto("/PerPerson/personalInfoNav/lastName", "default", ""));
		 * map.put("firstName", new
		 * ResultBuilderDto("/PerPerson/personalInfoNav/firstName", "default", ""));
		 * map.put("email", new
		 * ResultBuilderDto("/PerPerson/emailNav/emailAddress|COMPAREMAIL", "default",
		 * "")); map.put("technical_id", new
		 * ResultBuilderDto("/PerPerson/personIdExternal", "default", ""));
		 * map.put("middlename", new
		 * ResultBuilderDto("/PerPerson/personalInfoNav/middleName", "default", ""));
		 * map.put("disable_vault", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/customString19Nav/externalCode|LOOKUP|DISABLE_VAULT_LOOKUP",
		 * "default", "")); map.put("electronic_payslips_opted_out", new
		 * ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/locationNav/addressNavDEFLT/country|LOOKUP|OPT_OUT_FROM_ELECTRONIC_PAYSLIPS_LOOKUP",
		 * "default", "")); map.put("Network-ID", new
		 * ResultBuilderDto("/PerPerson/employmentNav/userNav/username", "default",
		 * "")); map.put("Employee-Status", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/emplStatusNav/externalCode|LOOKUP|Employee_Status",
		 * "default", "")); map.put("Employment-Type", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/employmentTypeNav/picklistLabels(locale='en_US')/label",
		 * "default", "")); map.put("Employee-Class", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/employeeClassNav/picklistLabels(locale='en_US')/label",
		 * "default", "")); map.put("Labor-Type", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/customString3Nav/picklistLabels(locale='en_US')/label",
		 * "default", "")); map.put("Original-Start-Date", new
		 * ResultBuilderDto("/PerPerson/employmentNav/originalStartDate|usFormat",
		 * "default", "")); map.put("Benefits-Service-Date", new
		 * ResultBuilderDto("/PerPerson/employmentNav/serviceDate|usFormat", "default",
		 * "")); map.put("Last-Start-Date", new
		 * ResultBuilderDto("/PerPerson/employmentNav/customDate2|usFormat", "default",
		 * "")); map.put("Job-Title", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/jobCodeNav/name",
		 * "default", "")); map.put("Global-Job-Code", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/customString29",
		 * "default", "")); map.put("Local-Job-Title", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/localJobTitle",
		 * "default", "")); map.put("Supervisor-ID", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/managerId", "default",
		 * "")); map.put("Location-Country", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/customString19Nav/picklistLabels(locale='en_US')/label",
		 * "default", "")); map.put("Location-Code", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/location", "default",
		 * "")); map.put("Location-Name", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/locationNav/name",
		 * "default", "")); map.put("external_id", new
		 * ResultBuilderDto("/PerPerson/personIdExternal", "default", ""));
		 * map.put("Region", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/customString19Nav/externalCode|LOOKUP|Region_Lookup_Table",
		 * "default", "")); map.put("Termination-Date", new
		 * ResultBuilderDto("/PerPerson/employmentNav/endDate|usFormat", "default",
		 * "")); map.put("Legal-Entity", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/companyNav/name_en_US|CONCATID|externalCode",
		 * "default", "")); map.put("Cost-Center", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/costCenter", "default",
		 * "")); map.put("Cost-Center-Name", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/costCenterNav/name",
		 * "default", "")); map.put("Headcount-Cost-Center", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/userNav/usersSysIdOfEmpCostDistributionNav(last)/items/costCenter",
		 * "default", "")); map.put("Headcount-Cost-Center-Name", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/userNav/usersSysIdOfEmpCostDistributionNav(last)/items/costCenterNav/name",
		 * "default", "")); map.put("Division", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/divisionNav/name|CONCATID|externalCode",
		 * "default", "")); map.put("Sub-Function", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/departmentNav/name|CONCATID|externalCode",
		 * "default", "")); map.put("Pay-Group", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/compInfoNav/payGroupNav/name|CONCATID|externalCode",
		 * "default", "")); map.put("Acquired-From-Company", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/acquiredFromCompanyNav/picklistLabels(locale='en_US')/label",
		 * "default", "")); map.put("US-PR-Tax-Location-Code", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/compInfoNav/customString7Nav/picklistLabels(locale='en_US')/label",
		 * "default", "")); map.put("Global-Assignment-Location", new
		 * ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/customString49",
		 * "default", "")); map.put("Supervisor-Name", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/managerUserNav/firstName|CONCATSPACE|lastName",
		 * "default", "")); map.put("HR-Manager-ID", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/empJobRelationshipNav(relationshipType='43065')/relEmploymentNav/personIdExternal",
		 * "default", "")); map.put("HR-Manager-Name", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/empJobRelationshipNav(relationshipType='43065')/relUserNav/firstName|CONCATSPACE|lastName",
		 * "default", ""));
		 */
		// map.put("city", new
		// ResultBuilderDto("/PerPerson/homeAddressNavDEFLT/city|LOOKUP|tbl1City",
		// "default", ""));
		// map.put("address1", new
		// ResultBuilderDto("/PerPerson/homeAddressNavDEFLT/address1", "default", ""));
		// map.put("country-retention", new
		// ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/countryOfCompany|withGBEmp|iso2",
		// "default", ""));
		// map.put("jobTitle", new
		// ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/jobTitle|withGBEmp",
		// "default", ""));
		// map.put("external_id", new ResultBuilderDto("/PerPerson/personIdExternal",
		// "default", ""));
		// map.put("custom_lapin", new
		// ResultBuilderDto("/PerPerson/personalInfoNav/firstName|CONCATSPACE|lastName",
		// "default", ""));
		// map.put("email", new ResultBuilderDto("/PerPerson/emailNav/emailAddress",
		// "default", ""));
		// map.put("zip_code", new
		// ResultBuilderDto("PerPerson/homeAddressNavDEFLT/zipCode", "default", ""));
		// map.put("title", new
		// ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/fte|x100", "default",
		// ""));
		// map.put("title", new
		// ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/jobTitle", "default",
		// ""));
		// map.put("location", new
		// ResultBuilderDto("/PerPerson/employmentNav/jobInfoNav/locationNav/name|CONCATID|externalCode",
		// "default", ""));
		// map.put("hr", new
		// ResultBuilderDto("/PerPerson/employmentNav/empJobRelationshipNav(relationshipType='119188')/relUserNav/firstName|CONCATSPACE|lastName",
		// "default", ""));
		// map.put("hr", new
		// ResultBuilderDto("/PerPerson/personRerlationshipNav(relationshipType='21244')/relatedPersonIdExternal|COUNT",
		// "default", ""));
		/*
		 * map.put("tableReturn", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/userId|TABLUSERID|startDate,customString1,empCompensationGroupSumCalculatedNav.amount,empCompensationGroupSumCalculatedNav.payComponentGroupId,empCompensationGroupSumCalculatedNav.currencyCode|EmpCompensation|startDate+desc",
		 * "default", "")); map.put("tableReturn2", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/userId|TABLUSERID|payDate,payComponentCode,value,currencyCode|EmpPayCompNonRecurring|payDate+desc",
		 * "default", ""));
		 */
		map.put("tableReturn2", new ResultBuilderDto(
				"/PerPerson/employmentNav/personIdExternal|TABLCOMP|employmentNav.startDate,employmentNav.empPayCompNonRecurringNav.payDate,employmentNav.empPayCompNonRecurringNav.payComponentCode,employmentNav.empPayCompNonRecurringNav.value,employmentNav.empPayCompNonRecurringNav.currencyCode,employmentNav.compInfoNav.startDate,employmentNav.compInfoNav.endDate,employmentNav.compInfoNav.empPayCompRecurringNav.seqNumber,employmentNav.compInfoNav.empPayCompRecurringNav.paycompvalue,employmentNav.compInfoNav.empPayCompRecurringNav.frequencyNav.annualizationFactor,employmentNav.compInfoNav.empPayCompRecurringNav.payComponent,employmentNav.compInfoNav.empPayCompRecurringNav.currencyCode|PerPerson",
				"default", ""));
		/*
		 * map.put("tableReturn2", new ResultBuilderDto(
		 * "/PerPerson/employmentNav/jobInfoNav/userId|TABLUSERID|startDate,companyNav.name,customString9Nav.name_defaultValue,departmentNav.name_defaultValue,payGradeNav.description, jobCodeNav.name,customString18|EmpJob|startDate+desc"
		 * , "default", ""));
		 */
		Map<String, ResultBuilderDto> mapRes = QueryBuilder.getInstance().convert(map, "10052671", "");

		return Response.status(200).entity(mapRes).build();
	}

	/*
	 * @GET
	 * 
	 * @Path("/mappingTest2")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response getAppMap2() {
	 * 
	 * Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
	 * 
	 * map.put("country", new
	 * ResultBuilderDto("/Background_Education/degreeNav/picklistLabels/label",
	 * "default", "")); map.put("city", new
	 * ResultBuilderDto("/Background_Education/school", "default", ""));
	 * map.put("custom_lapin", new
	 * ResultBuilderDto("/Background_Education/bgOrderPos", "default", ""));
	 * 
	 * Map<String, ResultBuilderDto> mapRes =
	 * QueryBuilder.getInstance().convert(map, "ehernandez", "ALL");
	 * 
	 * return Response.status(200).entity(mapRes).build(); }
	 */

	/*
	 * @GET
	 * 
	 * @Path("/lookvalue/{code}/{table}")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response
	 * getlookMap(@PathParam("code") String code, @PathParam("table") String table)
	 * {
	 * 
	 * LookupDAO lkdao = new LookupDAO();
	 * 
	 * LookupTable lk = lkdao.getLookupByInput(code, table);
	 * 
	 * return Response.status(200).entity(lk).build(); }
	 */

	/*
	 * @GET
	 * 
	 * @Path("/zip")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response getZip() {
	 * ResponseGenericDto attachResponseDto = null; attachResponseDto =
	 * SFOnboarding.test("ME_W_4ME", "6066a74c-c641-4d1c-82be-ace8feeec33d", true);
	 * 
	 * return Response.status(200).entity(attachResponseDto).build(); }
	 */

	/*
	 * @GET
	 * 
	 * @Path("/realuser")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response getReal() {
	 * 
	 * FieldsMappingPpdDAO fieldDAO = new FieldsMappingPpdDAO(); FieldsMappingPpd fi
	 * = fieldDAO.getFieldMappingByName("technical_id");
	 * 
	 * Map<String, ResultBuilderDto> map = new HashMap<String, ResultBuilderDto>();
	 * map.put("realuser", new ResultBuilderDto(fi.getNameDestination(), "default",
	 * "")); Map<String, ResultBuilderDto> mapRes =
	 * QueryBuilder.getInstance().convert(map, "ehernandez", "");
	 * 
	 * return
	 * Response.status(200).entity(mapRes.get("realuser").getResult()).build(); }
	 */

	/*
	 * @GET
	 * 
	 * @Path("/url")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response getUrl() {
	 * 
	 * String a = ""; try { a = PpdHttpConnectorV1.getInstance().getUrl(); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * return Response.status(200).entity(a).build(); }
	 */

	@GET
	@Path("/forceRoles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getforceroles() {
		EventListenerRolesJob a = new EventListenerRolesJob();
		a.force();
		return Response.status(200).entity("ok").build();
	}

	@GET
	@Path("/cleanRoles/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCleanRoles(@PathParam("id") Long id) {

		SFAdminDAO sfDao = new SFAdminDAO();
		List<SFAdmin> sfList = sfDao.getAll();

		for (SFAdmin sfAdmin : sfList) {
			if (sfAdmin.getAppsfroleid().equals(id))
				sfDao.delete(sfAdmin);
		}

		return Response.status(200).entity("ok").build();
	}

	@GET
	@Path("/cleanAllRoles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCleanAllRoles() {

		SFAdminDAO sfDao = new SFAdminDAO();
		List<SFAdmin> sfList = sfDao.getAll();

		for (SFAdmin sfAdmin : sfList) {

			sfDao.delete(sfAdmin);
		}

		return Response.status(200).entity("ok").build();
	}

	@GET
	@Path("/buildUserPpd/{idUser}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBuildUserPpd(@PathParam("idUser") String idUser) {

		SuccesFactorUserFacade userFacade = new SuccesFactorUserFacade();
		PpdCoreEmployeeInfoDto dto = userFacade.userPpdBuildDtoTest(idUser, null);

		Gson gson = new Gson();
		String vJsonPost = gson.toJson(dto);
		return Response.status(200).entity(vJsonPost).build();
	}

	@GET
	@Path("/orgaRefresh")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrga() {

		StructureBuilder.getInstance().updateLevelbyTime();

		// ArrayList<PpdCoreEmployeeOrgInfoDto> obj =
		// StructureBuilder.getInstance().getStructure("10060757",
		// "department|externalCode,name|ITERATIVE|parent", "2018-11-05", false,
		// concReturn);

		return Response.status(200).entity("ok").build();
	}
}