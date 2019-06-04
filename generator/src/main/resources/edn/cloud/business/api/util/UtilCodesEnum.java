package edn.cloud.business.api.util;

public enum UtilCodesEnum 
{
	//constants connectivity
	CODE_CONNECT_KEY_ARAGO("ARAGAO_KEY"),
	CODE_CONNECT_KEY_EMAIL_SEND_GRID("EMAIL_SEND_GRID_KEY"),
	CODE_CONNECT_VALUE_ARAGO("SjzGRZhhdC0TkQyS2f4gRCwljZE8bXKv!!"),
	CODE_DEFAULT_FROM_NOTI_EMAIL("no-reply@aragoconsulting.eu"),
	
	
	//Code result process
	CODE_OK("OK"),
	CODE_TRUE("true"),
	CODE_FALSE("false"),
	CODE_ERROR("ERROR"),
	CODE_ALL("ALL"),
	CODE_NA("NA"),
	CODE_NOTNULL("NOTNULL"),
	CODE_KEY_ENCRYPT_DATA("Passw0rd12E4S678"),
	
	CODE_ACTIVE("Active"),
	CODE_INACTIVE("Inactive"),
	CODE_ACTIVE_NUMBER("1"),
	CODE_INACTIVE_NUMBER("0"),
	
	//Code response http
	CODE_HTTP_200("","200","Success",""),
	CODE_HTTP_1501("","1501","Required Field",""),
	CODE_HTTP_1502("","1000","Error in Creation",""),
	
	CODE_SUCCESS_200("200"),
	CODE_SUCCESS_202("202"),
	CODE_ERROR_500("500"),
	CODE_ERROR_1001("1001"),
	CODE_ERROR_401("401"),
	CODE_ERROR_400("400"),
	CODE_ERROR_409("409"),
	CODE_ERROR_404("404"),
	CODE_HTTPS("HTTPS://"),
	
	//Code People doc
	CODE_PASSWS_DOWNLOAD_DOC("Sk6eLsffS95wg4U2FK9Hv6b77SrG5Om7"),	
	CODE_TOKEN_TYPE_BEARER("bearer"),
	CODE_STATUS_PENDING_DOC("Pending"),
	CODE_STATUS_COMPLETE_DOC("Complete"),
	CODE_STATUS_VALIDATE_DOC("Validated"),	
	CODE_STATUS_PENDING_SIGN_DOC("PendingSign"),	
	CODE_STATUS_SUCCESFULLCOMPLE_DOC("SuccessfullComplete"),
	CODE_STATUS_OTHEREASONCOMPLE_DOC("OtherReasonComplete"),	
	
	CODE_STATUS_PPD_SIGNATURE_PENDING("pending"),	
	CODE_STATUS_PPD_SIGNATURE_REJECTEDBYPPD("RejectedByPpd"),
	CODE_STATUS_PPD_SIGNATURE_SIGNED("signed"),	
	CODE_STATUS_PPD_SIGNATURE_SENT("sent"),
	CODE_STATUS_PPD_SIGNATURE_ARCHIVED("archived"),
	
	CODE_STATUS_PPD_SIGNATURE_CANCELED("canceled"),
	CODE_STATUS_PPD_SIGNATURE_REJECTED("rejected"),
	CODE_STATUS_PPD_SIGNATURE_EXPIRED("expired"),
	CODE_STATUS_PPD_SIGNATURE_ERROR("error"),
	
	//Code roles application
	
	CODE_APPROLE_EMPLOYEE("Employee"),
	CODE_APPROLE_MANAGER("Manager"),
	CODE_APPROLE_RECRUITER("Recruiter"),
	CODE_APPROLE_TEMPLATEADMIN("TemplateAdministrator"),
	CODE_APPROLE_USERADMIN("UserEmployeeAdministrator"),
	CODE_APPROLE_MASSIVEADMIN("MassiveLoadAdministrator"),
	CODE_APPROLE_EMAILACTOR("EmailActor"),
	CODE_APPROLE_FULLADMIN("FullAccessAdministrator"),
		
	CODE_PDF_DOC("PDF"),	
	
	//Status for update user in ppd
	CODE_STATUS_PENDING_UPDATE_USER("PendingUpdateUser"),
	
	//Code Sucessfactor
	DATE_SOURCE_USER_ACTIVE("9999-01-01"),
	CODE_DEFAULT("Default"),
	CODE_SIGN("\\sg"),
	CODE_NEW_LINE_SEPARATOR("\n"),
	CODE_SIGN_PRINCIPAL_CHAR("\\"),
	CODE_PATRON_SPLIT("@@x1hk"),
	CODE_PATRON_MAILUDPATE("??"),
	CODE_PATRON_HTMLFORMAT("[^a-zA-Z0-9\\p{IsHebrew}\\s)(\\[\\]\\-',:_@.]"),
	CODE_PATRON_NUMBERS("[^0-9)]"),
	CODE_PARAM_SEPARATOR_VALUEKEY("@@"),
	CODE_PARAM_SEPARATOR_SEMICOLON(";"),
	CODE_PARAM_SEPARATOR_COLON(","),
	CODE_INVALID_OR_NONE("-1"),
	CODE_SEPARATOR_OPERATION("|"),	
	CODE_OPERATION_REF("REF"),
	CODE_OPERATION_REF_DIS("REFDISABLED"),	
	CODE_EFFECTIVE_DATE("effectiveDate"),
	CODE_TODAY_DATE("today"),
	CODE_US_FORMAT("usFormat"),
	CODE_EU_FORMAT("euFormat"),
	CODE_DEFAULT_SEQ_EVENTLIS("1"),
	
		
	//Code Query Builder
	CODE_QUERYBUILDER_INVALID("Invalid"),
	
	//Codes
	CODE_TYPE_SIGNATURE_EMPLOYEE("employee"),
	CODE_TYPE_SIGNATURE_MANAGER("manager"),
	CODE_TYPE_SIGNATURE_EXTERNAL("external"),
	CODE_TYPE_SIGNATURE_DOCUSIGN("Docusign"),
	CODE_TYPE_SIGNATURE_OPENTRUST("Opentrust"),
	CODE_TYPE_SIGNATURE_FIRST_NAME("ExtFirstName"),
	CODE_TYPE_SIGNATURE_LAST_NAME("ExtLastName"),
	CODE_TYPE_SIGNATURE_EMAIL("ExtEmail"),
	CODE_TYPE_SIGNATURE_LANGUAGE_EN("en"),

	CODE_TYPE_DOWNLOAD("Download"),
	CODE_TYPE_COMPANY_VAULT("CompanyVault"),
	CODE_TYPE_EMPLOYEE_VAULT("EmployeeVault"),
	CODE_TYPE_FILE_NAME("FN"),
	CODE_TYPE_FILE_DT("DT"),
	CODE_TYPE_FILE_PI("PI"),
	CODE_TYPE_SYNC("SYNC"),
	CODE_TYPE_DEFAULT_PREFIX_USER("USR_@@"),
	CODE_TYPE_DATA_STRING("string"),
	CODE_TYPE_DATA_NUMBER("number"),
	CODE_TYPE_DATA_BOOLEAN("boolean"),
	
	//Code for Actions
	CODE_ACTIONS_ACT1_UPDATE_PROFILEUSER("ACT1"),
	CODE_ACTIONS_ACT2_APPLY_TYPE_DATA("ACT2"),
	CODE_ACTIONS_ACT3_ADD_MINUTES_DATE("ACT3"),
	
	//format structure user
	CODE_FORMAT_STRUCTURE_USER("STRUCTURE_USERID"),
	
	//Code Connector
	CODE_NOTIF_SF("http://notification.event.successfactors.com"),
	
	//event listener Codes
	CODE_STATUS_EVENTLIS_DEFAULT_MASSLOAD("event.default.massload"),
	
	//Status Event Listener

	CODE_STATUS_EVENTLIS_ERRORPPD("ErrorPpd"),
	CODE_STATUS_EVENTLIS_ERRORFIELD("ErrorFields"),
	CODE_STATUS_EVENTLIS_ERROR("Error"),
	CODE_STATUS_EVENTLIS_DOC_NOEXIST("NoExist"),
	CODE_STATUS_EVENTLIS_TIMEOUT("TimeOut"),
	CODE_STATUS_EVENTLIS_TERMINATE_EMP_ERROR_ATTACH("","TerminateErrorAttach","Employee completed with attach error",""),
	
	CODE_STATUS_EVENTLIS_TRANSFER_ATTACH("TransferAttach"),	
	CODE_STATUS_EVENTLIS_TERMIANTEBYUSER("TerminateByUser"),
	CODE_STATUS_EVENTLIS_TERMIANTE("","Terminate","Action Completed",""),	
	CODE_STATUS_EVENTLIS_TERMIANTEBYRETRIES("","TerminateByRetries","Failed After Many Attempts",""),
	CODE_STATUS_EVENTLIS_TERMIANTESENDTOSIGN("TerminateSendToSign"),
	CODE_STATUS_EVENTLIS_TERMIANTEFILEALREADY("TerminateFileAlready"),
	CODE_STATUS_EVENTLIS_TERMIANTEDOCCREATE("TerminateDocCreate"),
	CODE_STATUS_EVENTLIS_NOTPROCESS("notProcess"),
				
	CODE_STATUS_EVENTLIS_PENDING("Pending"),
	CODE_STATUS_EVENTLIS_PENDING_PROCCESS_AGAIN("","PendingProcAgain","Pending Process Again",""),
	CODE_STATUS_EVENTLIS_PENDING_VALIDATE("PendingValidate"),
	CODE_STATUS_EVENTLIS_PROCESSING("Processing"),		
	CODE_STATUS_EVENTLIS_SEARCHDOCS("SearchDocuments"),
	CODE_STATUS_EVENTLIS_USERPENDINGMAIL("UserPendingMail"),	
	
	//status group event listener
	CODE_STATUS_GROUP_EVENTLIS_ALL("All"),
	CODE_STATUS_GROUP_EVENTLIS_ERROR("Errors"),
	CODE_STATUS_GROUP_EVENTLIS_ERRORPPD("ErrorPpd"),
	CODE_STATUS_GROUP_EVENTLIS_PENDING("Pending"),
	CODE_STATUS_GROUP_EVENTLIS_SUCCESS("Successful"),
		
	
		
	//status massive load employee
	CODE_STATUS_MASSIVEEMPLE_TERMIANTE("Terminate"),
	CODE_STATUS_MASSIVEEMPLE_LOADED("Loaded"),
	CODE_STATUS_MASSIVEEMPLE_PENDING("Pending"),
	CODE_STATUS_MASSLOAD_LOADING("Loading"),
	CODE_STATUS_MASSIVEEMPLE_PROCESSING("Processing"),
	CODE_STATUS_MASSIVEEMPLE_TERMIANTEBYUSER("TerminateByUser"),
	
	
	//Modules Type Attach
	CODE_TYPE_MODULE_ADMIN_PARAM("ADM"),
	CODE_TYPE_MODULE_EMPLOYEE_CENTER("EMC"),
	CODE_TYPE_MODULE_RECRUITMENT("REC"),
	CODE_TYPE_MODULE_ONBOARDING("ONB"),
	CODE_TYPE_MODULE_ONBOARDINGV2("ONB-V2"),
	CODE_TYPE_MODULE_TEMPLATE("TMP"),
	CODE_TYPE_MODULE_ATTACH_UPDATE_USER("UpdateUserPpd"),
	
	//Constants Parameters	
	CODE_MASSIVE_EMAIL_SUBJECT("PSF Massive Load Report"),
	CODE_AUTHO_EMAIL_SUBJECT("PSF Authorization request / Generation of Document"),
	CODE_MASSIVE_SYNC_EVENT_SUBJECT("PSF Event/Synchronization Report"),
	
	CODE_PARAM_ADM_SEARCH_ATTACH_INIT_DATE_MASSIVE("1900-01-01 00:00"),
	CODE_PARAM_ADM_DEFAULT_MAX_RETRIES("1"),
	CODE_PARAM_ADM_MASSIVE_MAX_REG_PER_FILE("5000"),	
	CODE_PARAM_ADM_MASSIVE_EMAIL_SUBJECT("massive_email_subject"),	
	CODE_PARAM_ADM_MASSIVE_DOWNLOAD_URL("massive_download_url"),
	CODE_PARAM_ADM_STRUCTURE_KEY("structure"),
	CODE_PARAM_ADM_STRUCTURE_ORGANIZATION("structure_organization"),
	CODE_PARAM_ADM_MAX_RETRIES("max_retries_event"),
	CODE_PARAM_ADM_MAX_ATTACH("max_retries_attach"),
	CODE_PARAM_ADM_STATUS_LIMIT_RETRIES("status_limit_retries"),
	CODE_PARAM_ADM_TIME_INTERVAL_SEARCH_ATTCH("time_interval_search_attach"),
	CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH("time_initdate_search_attach"),
	CODE_PARAM_ADM_TIME_INITDATE_SEARCH_ATTACH_ADD("add_min_time_initdate_search_attach"),
	CODE_PARAM_ADM_PATTERN_FILENAME_ONBV2("pattern_filename_onbv2"),
	CODE_PARAM_ADM_PATTERN_FILENAME_SEND_PPD("pattern_filename_send_ppd"),
	CODE_PARAM_ADM_QUARZT_MAX_EVENT("max_reg_event"),
	CODE_PARAM_ADM_QUARZT_MAX_MASS_EMPL("max_mass_empl"),	
	CODE_PARAM_ADM_QUARZT_MAX_ATTACH("max_reg_attach"),
	CODE_PARAM_ADM_REG_PER_PAGE("max_record_per_page"),
	
	
	CODE_PARAM_ADM_OTHER_KEY_ONB("other_unique_key_onb"),	
	CODE_PARAM_ADM_TIME_INTERVAL_REG_JOBSIGN("time_interval_search_signstatus_job"),
	CODE_PARAM_ADM_QUARZT_MAX_REG_JOBSIGN("max_reg_proccess_signstatus_job"),	
	CODE_PARAM_ADM_QUARZT_MAX_WAITIME("max_wait_time_event"),
	CODE_PARAM_ADM_CUSTOM_ADMIN_FIELDS("custom_admin_fields"),
	CODE_PARAM_ADM_UPDATE_ORGA("update_orga_adm"),	
	CODE_PARAM_ADM_LAST_EXE_EVENTLIST("last_execution_event_listener"),
	CODE_PARAM_ADM_LAST_EXE_MASSIVE("last_execution_massive_load"),	
	CODE_PARAM_ADM_LAST_EXE_SEARCHATTACH("last_execution_job_search_attach"),
	CODE_PARAM_ADM_LAST_EXE_EVENTLISTATTACH("last_execution_job_send_attach"),
	CODE_PARAM_ADM_SIGNTECHNO("signature_technology"),
	CODE_PARAM_ADM_SIGNTECHNO_OPENTRUST("signature_technology_opentrust"),
	CODE_PARAM_ADM_SIGNEXPI("signature_expiration"),
	CODE_PARAM_ADM_SIGNCALLBACK_URL("signature_callback_url"),	
	CODE_PARAM_ADM_SIGNAUTO_EVENTLIST("signature_automatic_job_send_attach"),
	CODE_PARAM_ADM_WAITTIME_USERNOMAIL("wait_time_generation_usermail"),
	CODE_PARAM_ADM_PROCESS_USERNOMAIL("process_no_usermail"),
	CODE_PARAM_ADM_FORMAT_MAILUSERTERM_PPD("mail_format_user_terminate"),
	CODE_PARAM_ADM_ADD_DAY_TIME_PROCESS_AGAIN("add_day_time_process_again"),
	CODE_PARAM_AUTO_SENDDOC_SIGNALL_PPD("signature_senddoc_signfinish"),
	CODE_PARAM_RECRUITING_VISIBILITY("recruiting_visibility"),
	CODE_PARAM_TEMPLATES_VISIBILITY("templates_visibility"),
	CODE_PARAM_VIEWALL_VISIBILITY("viewall_visibility"),
	CODE_PARAM_ADMIN_VISIBILITY("administration_visibility"),
	CODE_PARAM_OPTIONS_VERSION_SIGNATURE("adm_options_ver_sign"),
	CODE_PARAM_TEAM_VISIBILITY("team_visibility"),	
	CODE_PARAM_ADM_CALL_UPDATEUSER_AFTER_CREATE("update_user_after_createcore_event_listener"),
	CODE_PARAM_MAX_DAYS_STORE_LOGG_RECORD("max_time_store_logger_record"),
	CODE_PARAM_REFERENCEID_USERID("user_reference_combination"),
	CODE_PARAM_REFERENCEID_COUNTRYID("user_reference_countryparameter"),
	CODE_PARAM_STRUCTURE_LOWERCASE("user_structure_lowercase"),
	CODE_PARAM_MANAGER_ROLE_PPD("manager_role_ppd"),
	CODE_PARAM_MANAGER_ORGANIZATION_PPD("manager_orga_ppd"),
	CODE_PARAM_PREFIX_USER_CREATE_PPD("prefix_user_create_ppd"),
	CODE_PARAM_LOG_SHOW_RESPONSE_PPD_EMPL("log_show_response_ppd_empl"),
	CODE_PARAM_MAX_TIME_DOC_AUTO_ARCHIVE("max_time_document_auto_archive"),
	
	//notifications
	CODE_PARAM_ADM_LAST_DATE_EMAIL_NOTI_EVENT("email_send_last_date_noti_event"),
	CODE_PARAM_ADM_EMAIL_NOTIFICATIONS("email_send_notifications"),
	CODE_PARAM_ADM_EMAIL_NOTIFICATIONS_HOUR("email_send_notifications_hour"),
	CODE_PARAM_ADM_EMAIL_INIT_HOUR("email_send_search_init_date"),
	CODE_PARAM_ADM_EMAIL_END_HOUR("email_send_search_end_date"),	
	
	//Groups parameters system
	CODE_PARAM_GRP_SIGN("Signature"),
	CODE_PARAM_GRP_EVENTLIST("EventListener"),
	CODE_PARAM_GRP_MASSLOAD("MassiveLoad"),
	CODE_PARAM_GRP_STRUCTURE("Structure"),
	CODE_PARAM_GRP_FRONT("Front - Views - Reports"),
	CODE_PARAM_GRP_ATTACH_JOB("AttachmentJob"),
	CODE_PARAM_GRP_ADM_JOB("Admin"),
	CODE_PARAM_GRP_ADM_LOG("Log"),	
	CODE_PARAM_GRP_ADM_NOT_CONFIGURE("Parameters Not Configured"),
	
	//Groups Fields Mapping
	CODE_MAPPING_GROUP_USER("UsersPpd"),
	CODE_MAPPING_GROUP_ATTACH("Attachments"),
	CODE_MAPPING_MAIL_PER("emailPersonal"),
	CODE_MAPPING_MAIL("email"),
	CODE_MAPPING_OPERA_REF("REF"),
	CODE_MAPPING_REF_DOCTYPE("DOCTYPE"),
	
	
	//Strings and Formats
	CODE_TIME_ZONE_UNIVERSAL("Universal"),
	CODE_STRING_INIT("_INIT"),
	CODE_STRING_END( "END_"),
	CODE_FORMAT_DATE("yyyy-MM-dd HH:mm"),
	CODE_FORMAT_DATE_CODE("yyyyMMddHHmm"),
	CODE_FORMAT_HOUR("HH:mm:ss"),	
	CODE_FORMAT_DATE_WITHOUT_HOUR("yyyy-MM-dd"),
	CODE_FORMAT_DATE_WITHOUT_HOUR_F1("dd/MM/yyyy"),//EU
	CODE_FORMAT_DATE_WITHOUT_HOUR_F2("MM/dd/yyyy"),//US
	
		
	//List Table	
	CODE_ENTITY_ADMINPARAMETERS("ADMP"),
	CODE_ENTITY_FIELDSMAPPINGPPD("FMPD"),
	CODE_ENTITY_FIELDSTEMPLATELIBRARY("FTMPL"),
	CODE_ENTITY_PARAMEVENTLISTENERS("PEL"),
	CODE_ENTITY_COUNTRY("COU"),
	CODE_ENTITY_SIGNTEMPLATELIBRARY("STL"),
	CODE_ENTITY_EVENTLIST("TABLE_EVENTLIST"),
	CODE_ENTITY_EMPLOYEE_SYNC("TABLE_EMPLOYEE_SYNC"),
	CODE_ENTITY_MASSIVE_LOAD("TABLE_MASSIVE_LOAD"),
	CODE_ENTITY_LOGGERS("ERRORLOGS"),
	CODE_ENTITY_LANAGUAGE("LANG"),
	CODE_ENTITY_ROLESMAPPINGPPD("ROL"),
	CODE_ENTITY_TEMPLATE("TMP"),
	CODE_ENTITY_FOLDERS_TEMPLATE("FLD"),
	CODE_ENTITY_LOOKUPTABLE("LKT"),
	CODE_ENTITY_BUSINESS_STRUCTURE("BSC") ,
	CODE_ENTITY_MANAGER_ROLE("MNR"),
	CODE_ENTITY_TABLE_AUTHO("TABLE_AUTHO"),
	//Name Tables

	CODE_TABLE_EVENTLIST("EVENTLISTENER_CTRL_PROCS"),	
	CODE_TABLE_EVENTLIST_ATTACH("EVENTLISTENER_DOC_PROCS_N"),
	CODE_TABLE_EMPLOYEE_SYNC("EVENTLISTENER_DOC_HISTO_N"),	
	
	//Source
	CODE_SOURCE_EVENTLISTENER("Event Listener Ctrl"),
	CODE_SOURCE_ATHORIZATION("Authorization Ctrl"),
	CODE_SOURCE_DOC_SELF("MySelf Generation"),
	
	//Names Job
	CODE_JOB_ATTACH_EVENT_LIST("JobAttachEvenLS"),
	CODE_JOB_ATTACH("JobAttachSend"),
	CODE_JOB_ATTACH_SEARCH("JobAttachSr"),
	CODE_JOB_EVENT_LIST("JobEventList"),
	CODE_JOB_MASS_EMPL("JobMassiveEmpl"),
	CODE_JOB_SIGNATURE("JobSignSt"),	
	CODE_JOB_ROLES("jobRoles"),
	CODE_JOB_ORGAS("jobOrgas"),
	
	//Control Panel
	CODE_CONTROLP_ENABLE_JOB_EVENT_LISTENER("code_controlp_enable_job_eventlist"),
	CODE_CONTROLP_ENABLE_JOB_EMPLOYEE_SYNC("code_controlp_enable_job_employeesync"),
	CODE_CONTROLP_ENABLE_JOB_LOAD_MASSIVE_EMPL("code_controlp_enable_load_massive_empl"),
	
	//Mapping Role Ppd
	CODE_ROLE_MAPPING_ORGANIZATION("organization"),
	CODE_ROLE_MAPPING_GROUP_ORGANIZATION("organization_group"),
	CODE_ROLE_MAPPING_LIST_ORGANIZATION("organization_list"),
	CODE_ROLE_MAPPING_OPERATOR_1("="),
	CODE_ROLE_MAPPING_OPERATOR_2("<="),
	CODE_ROLE_MAPPING_OPERATOR_3("<>"),
	
	//Manager Authorization
	CODE_MANAGER_AUTHORIZED("Authorized"),
	CODE_MANAGER_PENDING("Pending"),
	CODE_MANAGER_REJECTED("Rejected"),
	CODE_MANAGER_CANCELED("Canceled"),
	CODE_MANAGER_GENERATED("Generated"),//document generated
	CODE_MANAGER_ERROR_GENERATED("ErrorGenerated"),
	
	//Manager type Authorization
	CODE_MANAGER_TYPE_AUTHORIZATION("Authorization"),
	CODE_MANAGER_TYPE_SIGNATURE("Signature"),
	CODE_MANAGER_TYPE_NONE("None"),
	;
	
	
	//-------------------------------------------------------	
	private final String code ;
	private final String number;
	private final String label;
	private final String message;
	
	private UtilCodesEnum(String code) {
		this.code = code;
		this.label = "";		
		this.number = "";
		this.message = "";
	}

	private UtilCodesEnum(String number, String code, String label, String message) {
		this.code = code;
		this.label = label;
		this.number = number;
		this.message = message;
	}

	public String getCode() {
		return code;
	}
	
	public Integer getCodeInt() {
		return (Integer.parseInt(code));
	}	
	
	public Long getCodeLong() {
		return (Long.parseLong(code));
	}

	public String getLabel() {
		if(label!=null && !label.equals(""))
			return label;
		
		return code;
	}

	public String getNumber() {
		return number;
	}

	public String getMessage() {
		return message;
	}
	
	
}
