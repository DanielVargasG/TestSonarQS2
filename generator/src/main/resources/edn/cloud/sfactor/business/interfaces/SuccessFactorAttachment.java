package edn.cloud.sfactor.business.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.sfactor.persistence.entities.EventListenerDocHistory;
import edn.cloud.sfactor.persistence.entities.EventListenerDocProcess;
import edn.cloud.sfactor.persistence.entities.FieldsMappingPpd;

public interface SuccessFactorAttachment 
{
	/**
	 * search in success factor by module
	 * @param String idExternalEmplSearch
	 * @param FieldsMappingPpd attachModule 
	 * @param Date initDate
	 * @param Date endDate
	 * @param int numRegis
	 * */
	public ArrayList<EventListenerDocProcess> searchModifiedAttachments(String idExternalEmplSearch,FieldsMappingPpd attachModule,Date initDate,Date endDate, int numRegis)  ;
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * search attachment pending to process
	 * @param String statusToLoad
	 * @param String attachmentIdSF
	 * @return Boolean
	 * */
	public Boolean isAttachmentPendingToProcess(String statusToLoad,String attachmentIdSF);
	
	
	/**
	 * 
	 * 
	 * 
	 * Get all document attachment
	 * @return Collection<EventListenerDocHistory>
	 * */
	public ArrayList<EventListenerDocHistory> getAllAttachmentHisto();
	
	
	/***
	 * 
	 * 
	 * delete all attachments History
	 * */
	public void deleteAllAttachmentHisto();
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Updates id job process in eventlistener doc process
	 * @param Long idCtrlDoc
	 * @param Long idJobProcess
	 * */
	public void attachmentUpdateIdJob(Long idCtrlDoc,Long idJobProcess);
	
	/**
	 * get status count 
	 * @param filter 
	 * @return List<String[]>
	 * @return
	 */
	public List<Object[]> attachmentHistoGetStatusCount(FilterQueryDto filter);	
}
