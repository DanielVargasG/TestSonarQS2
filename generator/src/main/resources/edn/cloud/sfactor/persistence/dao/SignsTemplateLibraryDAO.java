package edn.cloud.sfactor.persistence.dao;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.SignsTemplateLibrary;

public class SignsTemplateLibraryDAO  extends BasicDAO<SignsTemplateLibrary>{

	public SignsTemplateLibraryDAO() {
		super(EntityManagerProvider.getInstance());
	} 
}
