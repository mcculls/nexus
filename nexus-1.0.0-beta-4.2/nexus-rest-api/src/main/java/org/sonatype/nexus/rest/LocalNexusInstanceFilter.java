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
package org.sonatype.nexus.rest;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.sonatype.nexus.Nexus;

/**
 * A restlet Filter, that handles "instanceName" attribute to put the correspondent (local or remote/proxied) Nexus
 * instance into request attributes, hance making local and remote call transparent in underlying restlets.
 * 
 * @author cstamas
 */
public class LocalNexusInstanceFilter
    extends NexusInstanceFilter
{
    /**
     * The filter constructor.
     * 
     * @param context
     */
    public LocalNexusInstanceFilter( Context context )
    {
        super( context );
    }

    /**
     * A beforeHandle will simply embed in request attributes the local Nexus instance.
     */
    protected void beforeHandle( Request request, Response response )
    {
        request.getAttributes().put( Nexus.ROLE, getLocalNexus() );
    }
}
