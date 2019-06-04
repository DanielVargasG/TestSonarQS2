package edn.cloud.sfactor.business.utils;

import edn.cloud.business.api.util.UtilLogger;

public class PermissionBuilder {

	private static PermissionBuilder INSTANCE = null;
	private final UtilLogger logger = UtilLogger.getInstance();
	
	
	public static synchronized PermissionBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PermissionBuilder();
		}
		return INSTANCE;
	}
	
	public void setup(String userId) { 
		logger.info("setup roles");
	}
}
