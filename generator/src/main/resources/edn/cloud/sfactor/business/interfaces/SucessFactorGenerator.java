package edn.cloud.sfactor.business.interfaces;

import java.util.List;

import edn.cloud.sfactor.persistence.entities.Generated;

public interface SucessFactorGenerator 
{
	/**
	 * get list generated by id document
	 * @param Long idDoc
	 * */
	public List<Generated> getGeneratedByIdDoc(Long idDoc);
	
	 /** save generated document
	 * @param Generated generateDoc
	 * */
	public Generated saveGeneratedDocument(Generated generateDoc);	
	
	/**
	 * get generated by id
	 * @param Long idGenerated
	 * */
	public Generated getGeneratedById(Long idGenerated);	
	

}
