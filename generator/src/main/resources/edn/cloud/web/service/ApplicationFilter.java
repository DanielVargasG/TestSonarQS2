package edn.cloud.web.service;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import edn.cloud.business.dto.odata.UserManager;

public class ApplicationFilter implements Filter {
	
	public ApplicationFilter() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String userId = null;
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			Principal userPrincipal = httpRequest.getUserPrincipal();
			if (userPrincipal != null) {
				userId = userPrincipal.getName();
				boolean isAdminUser = httpRequest.isUserInRole(ApplicationRoles.ADMINISTRATOR_ROLE);
				boolean isSuperAdminUser = httpRequest.isUserInRole(ApplicationRoles.SUPERADMIN_ROLE);
				UserManager.setUserId(userId);
				UserManager.setIsUserAdmin(isAdminUser);
				UserManager.setIsUserSuperAdmin(isSuperAdminUser);
			
				// pass the request along the filter chain
				chain.doFilter(request, response);
			} else {
				userId = null;
				boolean isAdminUser = httpRequest.isUserInRole(ApplicationRoles.ADMINISTRATOR_ROLE);
				boolean isSuperAdminUser = httpRequest.isUserInRole(ApplicationRoles.SUPERADMIN_ROLE);
				UserManager.setUserId(userId);
				UserManager.setIsUserAdmin(isAdminUser);
				UserManager.setIsUserSuperAdmin(isSuperAdminUser);
				
				chain.doFilter(request, response);
			}

		} finally {
			UserManager.cleanUp();
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
