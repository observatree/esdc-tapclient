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
package esac.archive.gacs.security.authentication;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import esac.archive.gacs.common.constants.HttpConstants;
import esavo.uws.storage.UwsQuotaSingleton;

public class  LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void  onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	
    	UwsQuotaSingleton.getInstance().removeUser(authentication.getName());
    	response.setStatus(HttpConstants.OK);
        //clearAuthenticationAttributes(request);
    }
    
}
