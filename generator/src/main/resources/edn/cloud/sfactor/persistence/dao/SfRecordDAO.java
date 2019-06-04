package edn.cloud.sfactor.persistence.dao;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;
import edn.cloud.sfactor.persistence.entities.SfRecord;

public class SfRecordDAO extends BasicDAO<SfRecord> {

	public SfRecordDAO() {
		super(EntityManagerProvider.getInstance());
	}
}
