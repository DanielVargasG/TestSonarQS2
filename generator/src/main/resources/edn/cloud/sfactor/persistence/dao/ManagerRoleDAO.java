package edn.cloud.sfactor.persistence.dao;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.ManagerRole;

public class ManagerRoleDAO extends BasicDAO<ManagerRole>{

	public ManagerRoleDAO() {
		super(EntityManagerProvider.getInstance());
	}
}
