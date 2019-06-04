package edn.cloud.web.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edn.cloud.sfactor.business.persistence.manager.EntityManagerFactoryProvider;
import edn.cloud.sfactor.business.persistence.manager.EntityManagerProvider;

public class AppServletContextListener implements ServletContextListener {

	protected EntityManagerProvider emProvider;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		EntityManagerFactoryProvider.getInstance().close();
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		try {
			EntityManagerProvider.getInstance().initEntityManagerProvider();
		} finally {
			EntityManagerProvider.getInstance().closeEntityManager();
		}
	}

}
