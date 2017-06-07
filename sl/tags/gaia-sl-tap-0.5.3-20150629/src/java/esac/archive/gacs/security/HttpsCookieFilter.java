/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package esac.archive.gacs.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpsCookieFilter implements Filter {

	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		System.out.println("HttpsCookieFilter: filtering!");
		
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		final HttpSession session = httpRequest.getSession(false);

		if (session != null) {
			final Cookie sessionCookie = new Cookie("JSESSIONID", session
					.getId());
			sessionCookie.setMaxAge(-1);
			sessionCookie.setSecure(false);
			sessionCookie.setPath(httpRequest.getContextPath());
			httpResponse.addCookie(sessionCookie);
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("HttpsCookieFilter: initializing!");
	}

}
