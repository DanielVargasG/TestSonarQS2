package edn.cloud.web.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edn.cloud.business.dto.integration.SFUserDto;
import edn.cloud.sfactor.business.facade.SuccessFactorFacade;
import edn.cloud.sfactor.persistence.dao.EmployeeDAO;
import edn.cloud.sfactor.persistence.entities.Employee;

@SuppressWarnings("nls")
public class SessionCreateFilter implements Filter {

	public static final String SF_USER_ID_ATTR_NAME = "sfUserId";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private SuccessFactorFacade successFactorFacade = new SuccessFactorFacade();

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String loggedInUser = httpRequest.getRemoteUser();

		if (loggedInUser != null) {
			initUserSession(loggedInUser, httpRequest);

		}

		filterChain.doFilter(request, response);
	}

	private void initUserSession(String loggedInUser, HttpServletRequest request) {
		Object userLock = UserLock.getInstance().getUserLock(loggedInUser);
		synchronized (userLock) { // Lock based on user prevents from concurrent
									// // user session initialization
			String initialFlag = (String) request.getSession().getAttribute(SessionListener.INITIAL_FLAG);
			if (initialFlag != null) {
				EmployeeDAO userDAO = getUserDAO();
				Employee user = initSingleUserProfile(loggedInUser, userDAO, request.getSession());

				if (request.isUserInRole(ApplicationRoles.ADMINISTRATOR_ROLE) && user != null) {
					// initManagedUsers(user, userDAO);
				}
				request.getSession().removeAttribute(SessionListener.INITIAL_FLAG);
			}
		}
	}

	private EmployeeDAO getUserDAO() {
		return new EmployeeDAO();
	}

	private Employee initSingleUserProfile(String userName, EmployeeDAO userDAO, HttpSession session) {
		try {

			session.setAttribute(SF_USER_ID_ATTR_NAME, userName);
			SFUserDto sfUser = successFactorFacade.userGetProfile(userName, "9999-01-01");
			// session.setAttribute(SF_USER_ID_ATTR_NAME, sfUser.callUserId());

			Employee user = null;
			
			if (sfUser != null) {

				String sfUserManagerCount = successFactorFacade.userGetManagerCount(userName, "9999-01-01");

				String sfUserHrCount = successFactorFacade.userGetHrCount(userName, "9999-01-01");

				 userDAO.getByUserId(sfUser.callUserId());
				if (user == null) {
					user = createNewUser(sfUser, userDAO);
				}

				user.setDefaultLanguage(sfUser.callDefLG());
				user.setCountMng(sfUserManagerCount);
				user.setCountHr(sfUserHrCount);
				userDAO.save(user);
			}
			

			return user;
		} catch (Exception ex) {
			logger.error("User '{}' could not be extracted from backend. The user will be initialized simply.", userName, ex);
			return createUser(userName, userDAO);
		}
	}

	private Employee createNewUser(SFUserDto sourceSfUser, EmployeeDAO userDAO) {
		Employee newUser = new Employee();
		sourceSfUser.write(newUser);
		userDAO.saveNew(newUser);
		return newUser;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	private Employee createUser(String userName, EmployeeDAO userDAO) {
		Employee user = userDAO.getByUserId(userName);
		if (user == null) {
			Employee newUser = new Employee(userName);
			userDAO.saveNew(newUser);
			return newUser;
		} else {
			return user;
		}
	}

}
