/*
 * Nexus: Maven Repository Manager
 * Copyright (C) 2008 Sonatype Inc.                                                                                                                          
 * 
 * This file is part of Nexus.                                                                                                                                  
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */
package org.sonatype.nexus.rest.users;

import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.sonatype.nexus.rest.model.UserForgotPasswordRequest;
import org.sonatype.nexus.rest.model.UserForgotPasswordResource;

public class UserForgotPasswordResourceHandler
    extends AbstractUserResourceHandler
{
    public UserForgotPasswordResourceHandler( Context context, Request request, Response response )
    {
        super( context, request, response );
    }

    @Override
    public boolean allowPost()
    {
        return true;
    }

    @Override
    public void post( Representation representation )
    {
        UserForgotPasswordRequest request = (UserForgotPasswordRequest) deserialize( new UserForgotPasswordRequest() );

        if ( request == null )
        {
            return;
        }
        else
        {
            UserForgotPasswordResource resource = request.getData();

            if ( !isAnonymousUser( resource.getUserId() ) )
            {
                /* TODO
                getNexusSecurityConfiguration().forgotPassword( resource.getUserId(), resource.getEmail() );
                */
                
                getResponse().setStatus( Status.SUCCESS_ACCEPTED );
            }
            else
            {
                getResponse().setStatus( Status.CLIENT_ERROR_BAD_REQUEST, "Anonymous user cannot forgot password!" );

                getLogger().log( Level.FINE, "Anonymous user forgot password is blocked!" );
            }
        }
    }
}
