package nl.menninga.menno.cms.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {


	/**
	 * Method for requesting roles of logged in user.
	 * 
	 * @param role {@link String}
	 * @return {@link boolean}
	 */
	public boolean hasRole(String role) {
		role = "ROLE_" + role;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean hasRole = false;
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			hasRole = authority.getAuthority().equals(role);
			if (hasRole) {
				break;
			}
		}
		return hasRole;
	}

}
