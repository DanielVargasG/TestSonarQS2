package edn.cloud.sfactor.persistence.dao;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.AuthorizationDocument;

public class AuthorizationDocumentDAO extends BasicDAO<AuthorizationDocument> {

	public AuthorizationDocumentDAO () {
		super(EntityManagerProvider.getInstance());
	}
}
