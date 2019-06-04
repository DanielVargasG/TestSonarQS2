package edn.cloud.sfactor.business.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import edn.cloud.business.dto.FilterQueryDto;
import edn.cloud.business.dto.SignatureFieldDto;
import edn.cloud.sfactor.persistence.entities.Document;
import edn.cloud.sfactor.persistence.entities.DocumentFields;
import edn.cloud.sfactor.persistence.entities.DocumentSignature;
public interface SucessFactorDocument 
{	
	/**
	 * delete all document without template
	 * */
	public void documentDeleteAllWithoutTemplate();
	
	/**
	 * find Document by id
	 * @param Long id
	 * */
	public Document getDocumentById(Long id);
	
	/**
	 * save documento new
	 * @param Document document
	 * @return Document
	 * */
	public Document saveDocument(Document document);
	
	/**
	 * get document signature by id doc
	 * @param Long id
	 * @return ArrayList<DocumentSignature>
	 * */
	public ArrayList<DocumentSignature> getDocumentSignatureByDoc(Long id);
	
	/**
	 * delete all document and dependencies
	 * @param Long idDoc
	 * @return Boolean 
	 * */
	public Boolean documentDelete(Long idDoc);	
	
	/**
	 * save new document Signature
	 * @param SignatureFieldDto signDto
	 * @return SignatureFieldDto
	 * */
	public SignatureFieldDto documentSignatureInsert(Long idDocument,SignatureFieldDto signDto);
	
	/**
	 * reset document Signature
	 * @param Long idSignDocument
	 * */
	public void documentSignatureReset(Long idSignDocument);
	
	/**
	 * update document Signature
	 * @param Long idDocument
	 * @param SignatureFieldDto signDto
	 * @return SignatureFieldDto
	 * */
	public SignatureFieldDto documentSignatureUpdate(Long idDocument,SignatureFieldDto signDto);
	
	/**
	 * save new document field
	 * @param DocumentFields docField
	 * @return DocumentFields
	 * */
	public DocumentFields documentFieldInsert(DocumentFields docField);
	
	/**
	 * reset document field
	 * @param Long idDocumentField
	 * */
	public void documentFieldReset(Long idDocumentField);
	
	/**
	 * update document field
	 * @param DocumentFields documentField
	 * @return DocumentFields
	 * */
	public DocumentFields documentFieldUpdate(DocumentFields documentField);	
	
	/**
	 * get all documents fields by id document
	 * @param Long idDocumentField
	 * @return ArrayList<DocumentFields>
	 * */
	public ArrayList<DocumentFields> documentFieldGetAllByIdDoc(Long idDocumentField);
	
	/**
	 * 
	 * Updates status of document
	 * @param Long idDocument
	 * @param String statusDocument
	 * */
	public void updateStatusDocument(Long idDocument, String statusDocument);
	
	/**
	 * 
	 * find all documents by status
	 * @param String idUser
	 * @param String status
	 * @return Collection<Document>
	 * **/
	public Collection<Document> findAllDocumentsByStatus(String idUser,String status);
	
	/**
	 * return document list filter by groups of user
	 * @param String idGroupsList
	 * @param String statusDocument	 / optional 
	 * @return List<Document>
	 **/
	public Collection<Document> getListByGroupUser(String idGroupsList,String statusDocument);	
	
	/**
	 * return document list associated with subfolders filter by groups of user
	 * @param String idGroupsList
	 * @param String statusDocument	 / optional 
	 * @return List<Document>
	 **/
	public Collection<Document> getListSubByGroupUser(String idGroupsList,String statusDocument);
	
	/**
	 * return document list filter by user template
	 * @param String idUser
	 * @param String statusDocument	 / optional
	 * @param FilterQueryDto dtoFilter 
	 * @return List<Document>
	 **/
	public Collection<Document> getListByUserTemplate(String idUser,String statusDocument,FilterQueryDto dtoFilter);
	
	/**
	 * return number of documents by user template
	 * @param String idUser
	 * @param String statusDocument	 / optional 
	 * @return Integer
	 **/
	public Integer getCountDocumentByUserTemplate(String idUser,String statusDocument);
	
	/**
	 * Updates status of document associated with signature control process
	 * @param Long idSignature
	 * @param String statusDocument
	 * */
	public void updateStatusDocumentByIdSign(Long idSignature, String statusDocument);
	
	/**
	 * Get document archived for date
	 * @param idUser
	 * @param statusDocument
	 * @param String date1
	 * @param String date2
	 * @return
	 */
	public Collection<Document> getDocumentByUserArchive(String idUser,String statusDocument, String date1, String date2, String userTarget);
	
	/**
	 * update removes all records with creation date less than the filter date
	 * @param String archiveStatus
	 * @param filterDate
	 * @return boolean
	 * */
	public boolean documentUpdateArchiveByMaxTime(String archiveStatus,Date filterDate);
}
	
	