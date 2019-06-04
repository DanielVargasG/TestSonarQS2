package edn.cloud.sfactor.business.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.business.interfaces.SuccessFactorAttachment;
import edn.cloud.sfactor.persistence.dao.EventListenerDocHistoDAO;
import edn.cloud.sfactor.persistence.dao.EventListenerDocProcessDAO;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.FieldsMappingMeta;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public class SuccessFactorAttachmentImpl implements SuccessFactorAttachment 
{
	
	/**
	 * search in success factor by module
	 * 
	 * @param idExternalEmplSearch **optional
	 * @param FieldsMappingPpd attachModule
	 * @param Date initDate
	 * @param Date endDate
	 * @param int numRegis
	 * @return ArrayList<EventListenerDocProcess>
	 */
	public ArrayList<EventListenerDocProcess> searchModifiedAttachments(String idExternalEmplSearch, 
																		FieldsMappingPpd attachModule,																		
																		Date initDate, 
																		Date endDate, 
																		int numRegis) 
	{
		//evaluate operations on dates
		if(attachModule.getActions()!=null){
			List<SlugItem> actionsList = attachModule.getActionsList("");
			if(actionsList!=null){
				for (SlugItem actionItem : actionsList){
					if (actionItem != null && actionItem.getId() != null && actionItem.getId().equals(UtilCodesEnum.CODE_ACTIONS_ACT3_ADD_MINUTES_DATE.getCode())) 
					{
						try{
							initDate = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp(initDate.getTime()), Long.parseLong(actionItem.getCode()) * -1);
							endDate = UtilDateTimeAdapter.getDateAddMinutes(new Timestamp(endDate.getTime()), Long.parseLong(actionItem.getCode()) * -1);
						}catch (Exception e) {
							// TODO: handle exception
						}
					}
				}				
			}
		}
		
		ArrayList<EventListenerDocProcess> resultDocProcess = new ArrayList<>();
		String dateInitUrl = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), initDate) + "T" + UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_HOUR.getCode(), initDate);

		// "2017-03-14T15:17:00";//

		String dateEndUrl = UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE_WITHOUT_HOUR.getCode(), endDate) + "T" + UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_HOUR.getCode(), endDate);

		if (endDate.getTime() < initDate.getTime()) {
			String tmp = dateInitUrl;
			dateInitUrl = dateEndUrl;
			dateEndUrl = tmp;
		}

		// prepare sql success factor
		String searchQuery = attachModule.getNameDestination().replaceAll("#date1", dateInitUrl);
		searchQuery = searchQuery.replaceAll("#date2", dateEndUrl);
		searchQuery = searchQuery.replaceAll("#numReg", numRegis + "");

		// include user if this exist
		if (idExternalEmplSearch != null && !idExternalEmplSearch.equals(""))
		{
			//validates that the query actually has the employee filter
			if(!searchQuery.contains("#userDirect")){
				return null;
			}
			
			searchQuery = searchQuery.replaceAll("#userDirect", "  and personIdExternal eq '" + idExternalEmplSearch + "' ");
			if(!searchQuery.contains(idExternalEmplSearch+"")){
				return null;
			}
			
			//only documents for specific user
			attachModule.setIsFilter(false);
				
		}
		else
			searchQuery = searchQuery.replaceAll("#userDirect", "");

		String resultQuery = "";

		try 
		{			
			resultQuery = HttpConnectorSuccessFactor.getInstance().executeGET(searchQuery);
		} 
		catch (Exception e) 
		{
			e.getMessage();
			return null;
		}
		
		SuccessFactorAdminImpl SFAdminImpl = new SuccessFactorAdminImpl();
		List<FieldsMappingMeta> metadata = SFAdminImpl.mappingPPdFieldsGetMetada(attachModule);
		resultDocProcess = UtilMapping.loadAttachmentsEmployeeCentral(resultQuery, attachModule,metadata, initDate, endDate);

		return resultDocProcess;

	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * search attachment pending to process
	 * 
	 * @param String
	 *            statusToLoad
	 * @param String
	 *            attachmentIdSF
	 * @return Boolean
	 */
	public Boolean isAttachmentPendingToProcess(String statusToLoad, String attachmentIdSF) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		return dao.isAttachmentPendingToProcess(statusToLoad, attachmentIdSF);
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates id job process in eventlistener doc process
	 * 
	 * @param Long
	 *            idCtrlDoc
	 * @param Long
	 *            idJobProcess
	 */
	public void attachmentUpdateIdJob(Long idCtrlDoc, Long idJobProcess) {
		EventListenerDocProcessDAO dao = new EventListenerDocProcessDAO();
		dao.eventListenerDocProcessUpdateIdJob(idCtrlDoc, idJobProcess);
	}

	/**
	 * 
	 * 
	 * 
	 * Get all document attachment
	 * 
	 * @return Collection<EventListenerDocHistory>
	 */
	public ArrayList<EventListenerDocHistory> getAllAttachmentHisto() {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		Collection<EventListenerDocHistory> li = dao.getAllAttachmentHisto();
		ArrayList<EventListenerDocHistory> responseList = new ArrayList<EventListenerDocHistory>(li);
		return responseList;
	}

	/***
	 * 
	 * 
	 * delete all attachments History
	 */
	public void deleteAllAttachmentHisto() {
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		dao.deleteAllAttachments();
	}
	
	/**
	 * get status count 
	 * @param filter 
	 * @return List<String[]>
	 * @return
	 */
	public List<Object[]> attachmentHistoGetStatusCount(FilterQueryDto filter){
		EventListenerDocHistoDAO dao = new EventListenerDocHistoDAO();
		return dao.getStatusCount(filter);
	}

}
