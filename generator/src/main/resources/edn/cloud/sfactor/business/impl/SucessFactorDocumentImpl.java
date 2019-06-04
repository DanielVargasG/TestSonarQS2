package edn.cloud.sfactor.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.business.interfaces.SucessFactorDocument;
import edn.cloud.sfactor.persistence.dao.DocumentDAO;
import edn.cloud.sfactor.persistence.dao.DocumentFieldsDAO;
import edn.cloud.sfactor.persistence.dao.DocumentSignatureDAO;
import edn.cloud.sfactor.persistence.dao.ErrorLogDAO;
import edn.cloud.sfactor.persistence.dao.GeneratedDAO;
import edn.cloud.sfactor.persistence.dao.SignatureFileControlDAO;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.DocumentFields;
import edn.cloud.sfactor.persistence.entities.DocumentSignature;
import edn.cloud.sfactor.persistence.entities.Generated;

public class SucessFactorDocumentImpl implements SucessFactorDocument {

	
	//Methods Document
	//---------------------------------------------------------------
	//---------------------------------------------------------------
	/**
	 * get document signature by id doc
	 * @param Long id
	 * @return ArrayList<DocumentSignature>
	 * */
	public ArrayList<DocumentSignature> getDocumentSignatureByDoc(Long id){
		DocumentSignatureDAO dao = new DocumentSignatureDAO();
		Collection<DocumentSignature> result = dao.getAllByDocument(id);
		ArrayList<DocumentSignature> resultArray = new ArrayList<>(result);
		return resultArray;
	}
	
	/**
	 * find Document by id
	 * @param Long id
	 * */
	public Document getDocumentById(Long id)
	{
		DocumentDAO dao = new DocumentDAO();
		Document response = dao.getById(id);
		return response;
	}
	
	/**
	 * 
	 * find all documents by status
	 * @param String idUser
	 * @param String status
	 * @return Collection<Document>
	 * **/
	public Collection<Document> findAllDocumentsByStatus(String idUser,String status){
		DocumentDAO dao = new DocumentDAO();
		return dao.findAllDocumentsByStatus(idUser, status);
	}
	
	
	/**
	 * 
	 * Updates status of document
	 * @param Long idDocument
	 * @param String statusDocument
	 * */
	public void updateStatusDocument(Long idDocument, String statusDocument){
		DocumentDAO dao = new DocumentDAO();
		dao.updateStatusDocument(idDocument, statusDocument);
	}
	
	/**
	 * Updates status of document associated with signature control process
	 * @param Long idSignature
	 * @param String statusDocument
	 * */
	public void updateStatusDocumentByIdSign(Long idSignature, String statusDocument) {
		DocumentDAO dao = new DocumentDAO();
		dao.updateStatusDocumentByIdSign(idSignature, statusDocument);
	}	
	
	/**
	 * return document list filter by groups of user
	 * @param String idGroupsList
	 * @param String statusDocument	 / optional 
	 * @return List<Document>
	 **/
	public Collection<Document> getListByGroupUser(String idGroupsList,String statusDocument){
		DocumentDAO dao = new DocumentDAO();
		return dao.getListByGroupUser(idGroupsList, statusDocument);
	}
	
	/**
	 * return number of documents by user template
	 * @param String idUser
	 * @param String statusDocument	 / optional 
	 * @return Integer
	 **/
	public Integer getCountDocumentByUserTemplate(String idUser,String statusDocument){
		DocumentDAO dao = new DocumentDAO();
		return dao.getCountDocumentByUserTemplate(idUser, statusDocument);
	}	
	
	/**
	 * return document list associated with subfolders filter by groups of user
	 * @param String idGroupsList
	 * @param String statusDocument	 / optional 
	 * @return List<Document>
	 **/
	public Collection<Document> getListSubByGroupUser(String idGroupsList,String statusDocument){
		DocumentDAO dao = new DocumentDAO();
		return dao.getListSubByGroupUser(idGroupsList, statusDocument);
	}
	
	/**
	 * return document list filter by user template
	 * @param String idUser
	 * @param String statusDocument	 / optional 
	 * @param FilterQueryDto dtoFilter
	 * @return List<Document>
	 **/
	public Collection<Document> getListByUserTemplate(String idUser,String statusDocument,FilterQueryDto dtoFilter){
		DocumentDAO dao = new DocumentDAO();
		
		return dao.getListByUserTemplate(idUser, statusDocument, dtoFilter);
	}
	
	/**
	 * save documento new
	 * @param Document document
	 * @return Document
	 * */
	public Document saveDocument(Document document){
		DocumentDAO dao = new DocumentDAO();
		document = dao.saveNew(document);
		return document;
	}
	
	/**
	 * delete all document without template
	 * */
	public void documentDeleteAllWithoutTemplate()
	{
		DocumentDAO dao = new DocumentDAO();
		Collection<Document> result = dao.findAllDocumentsWithOutTemplate();
		ArrayList<Document> resultArray = new ArrayList<>(result);
		
		if(resultArray!=null && resultArray.size()>0){
			for(Document doc:resultArray){
				this.documentDelete(doc.getId());
			}
		}		
	}
	
	/**
	 * delete all document and dependencies
	 * @param Long idDoc
	 * @param String listIdGenerate
	 * @return Boolean 
	 * */
	public Boolean documentDelete(Long idDoc)
	{
		SuccessFactorFacade successFactorFacade = new SuccessFactorFacade();
		DocumentSignatureDAO daoSignature = new DocumentSignatureDAO();
		SignatureFileControlDAO daoSignFileCtrl = new SignatureFileControlDAO();
		GeneratedDAO daoGenerate = new GeneratedDAO();		
		ErrorLogDAO daoError = new ErrorLogDAO();
		DocumentDAO daoDocument = new DocumentDAO();
		DocumentFieldsDAO daoDocumentField = new DocumentFieldsDAO();
		
		List<Generated> listGenerated = successFactorFacade.generatedGetByIdDoc(idDoc);
		String listIdGenerate = "";
		for(Generated item:listGenerated)
			listIdGenerate+=item.getId()+",";
			
		listIdGenerate = listIdGenerate!=""?listIdGenerate.substring(0,listIdGenerate.length()-1):"";
		
		if(daoDocumentField.deleteAllByDocId(idDoc) 
				&& daoSignature.deleteAllByDocId(idDoc) &&
					daoError.deleteAllByDocId(idDoc) &&
						daoSignFileCtrl.deleteAllByDocId(idDoc,listIdGenerate) &&
							daoGenerate.deleteAllByDocId(idDoc) && 
								daoDocument.deleteAllByDocId(idDoc))
		{
			return true;
		}
		
		return false;
	}
	
	
	//---------------------------------------------------------------------------
	//Document Signature Methods
	
	/**
	 * save new document Signature
	 * @param SignatureFieldDto signDto
	 * @return SignatureFieldDto
	 * */
	public SignatureFieldDto documentSignatureInsert(Long idDocument,SignatureFieldDto signDto)
	{
		DocumentSignatureDAO dao = new DocumentSignatureDAO();
		DocumentSignature signEntity = UtilMapping.dtoToDocumentSignature(signDto);
		
		signEntity.setDocumentId(new Document(idDocument));		
		signEntity = dao.saveNew(signEntity);
		
		signDto.setIdSignDocument(signEntity.getId());
		return signDto;
	}
	
	/**
	 * reset document Signature
	 * @param Long idSignDocument
	 * */
	public void documentSignatureReset(Long idSignDocument)
	{
		DocumentSignatureDAO dao = new DocumentSignatureDAO();
		dao.deleteDocumentSignatureById(idSignDocument);
	}
	
	/**
	 * update document Signature
	 * @param Long idDocument
	 * @param SignatureFieldDto signDto
	 * @return SignatureFieldDto
	 * */
	public SignatureFieldDto documentSignatureUpdate(Long idDocument,SignatureFieldDto signDto)
	{
		DocumentSignatureDAO dao = new DocumentSignatureDAO();
		DocumentSignature signEntity = UtilMapping.dtoToDocumentSignature(signDto);
		
		signEntity.setDocumentId(new Document(idDocument));		
		signEntity = dao.save(signEntity);
		
		signDto.setIdSignDocument(signEntity.getId());
		return signDto;
	}	
	
	/**
	 * Save signature asociate to document
	 * @param DocumentSignature entity
	 * @return id Signature Document
	 * */
	public Long documentSignatureSave(DocumentSignature entity)
	{
		DocumentSignatureDAO dao = new DocumentSignatureDAO();
		dao.saveNew(entity);
		return entity.getId();
	}	
	
	//--------------------------------------------------------------------
	//Document Fields Methods
	
	/**
	 * save new document field
	 * @param DocumentFields docField
	 * @return DocumentFields
	 * */
	public DocumentFields documentFieldInsert(DocumentFields docField){
		DocumentFieldsDAO dao = new DocumentFieldsDAO();
		return dao.saveNew(docField);
	}
	
	/**
	 * reset document field
	 * @param Long idDocumentField
	 * */
	public void documentFieldReset(Long idDocumentField){
		DocumentFieldsDAO dao = new DocumentFieldsDAO();
		dao.deleteDocumentFieldById(idDocumentField);
	}
	
	/**
	 * update document field
	 * @param Long idDocument
	 * @param DocumentFields documentField
	 * @return DocumentFields
	 * */
	public DocumentFields documentFieldUpdate(DocumentFields documentField){
		DocumentFieldsDAO dao = new DocumentFieldsDAO();
		return dao.save(documentField);
	}
	
	/**
	 * get all documents fields by id document
	 * @param Long idDocumentField
	 * @return ArrayList<DocumentFields>
	 * */
	public ArrayList<DocumentFields> documentFieldGetAllByIdDoc(Long idDocumentField){
		DocumentFieldsDAO dao = new DocumentFieldsDAO();
		Collection<DocumentFields> collResult =  dao.getAllByDocument(idDocumentField);
		return (new ArrayList<>(collResult));
	}
	
	/**
	 * Get document archived for date
	 * @param idUser
	 * @param statusDocument
	 * @param String date1
	 * @param String date2
	 * @return
	 */
	public Collection<Document> getDocumentByUserArchive(String idUser,String statusDocument, String date1, String date2, String userTarget){
		DocumentDAO dao = new DocumentDAO();
		Collection<Document> result = dao.getDocumentByUserArchive(idUser, statusDocument, date1, date2, userTarget);
		return result;
	}
	
	/**
	 * update removes all records with creation date less than the filter date
	 * @param String archiveStatus
	 * @return boolean
	 * */
	public boolean documentUpdateArchiveByMaxTime(String archiveStatus, Date filterDate) {
		DocumentDAO dao = new DocumentDAO();
		String idDocs = dao.documentUpdateArchiveByMaxTimeGetIds(archiveStatus, filterDate);
		return dao.documentUpdateArchiveByMaxTime(archiveStatus, filterDate,idDocs);		
	}
}
