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
package org.sonatype.nexus.rest.cache;

import org.codehaus.plexus.util.StringUtils;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.NoSuchRepositoryGroupException;
import org.sonatype.nexus.rest.AbstractNexusResourceHandler;

/**
 * @author cstamas
 */
public class CacheResourceHandler
    extends AbstractNexusResourceHandler
{
    public static final String DOMAIN = "domain";

    public static final String DOMAIN_REPOSITORIES = "repositories";

    public static final String DOMAIN_REPO_GROUPS = "repo_groups";

    public static final String TARGET_ID = "target";

    protected String repositoryId;

    protected String repositoryGroupId;

    protected String resourceStorePath;

    public CacheResourceHandler( Context context, Request request, Response response )
    {
        super( context, request, response );

        resourceStorePath = null;

        repositoryId = null;

        repositoryGroupId = null;

        if ( getRequest().getAttributes().containsKey( DOMAIN ) && getRequest().getAttributes().containsKey( TARGET_ID ) )
        {
            if ( DOMAIN_REPOSITORIES.equals( getRequest().getAttributes().get( DOMAIN ) ) )
            {
                repositoryId = getRequest().getAttributes().get( TARGET_ID ).toString();
            }
            else if ( DOMAIN_REPO_GROUPS.equals( getRequest().getAttributes().get( DOMAIN ) ) )
            {
                repositoryGroupId = getRequest().getAttributes().get( TARGET_ID ).toString();
            }
        }

        if ( repositoryId != null || repositoryGroupId != null )
        {
            resourceStorePath = getRequest().getResourceRef().getRemainingPart();

            // get rid of query part
            if ( resourceStorePath.contains( "?" ) )
            {
                resourceStorePath = resourceStorePath.substring( 0, resourceStorePath.indexOf( '?' ) );
            }

            // get rid of reference part
            if ( resourceStorePath.contains( "#" ) )
            {
                resourceStorePath = resourceStorePath.substring( 0, resourceStorePath.indexOf( '#' ) );
            }

            if ( StringUtils.isEmpty( resourceStorePath ) )
            {
                resourceStorePath = "/";
            }
        }
    }

    public boolean allowGet()
    {
        return false;
    }

    public boolean allowDelete()
    {
        return true;
    }

    public void delete()
    {
        try
        {
            // check reposes
            if ( repositoryGroupId != null )
            {
                getNexus().readRepositoryGroup( repositoryGroupId );
            }
            else if ( repositoryId != null )
            {
                try
                {
                    getNexus().readRepository( repositoryId );
                }
                catch ( NoSuchRepositoryException e )
                {
                    getNexus().readRepositoryShadow( repositoryId );
                }
            }

            getScheduler()
                .submit( new ClearCacheTask( getNexus(), repositoryId, repositoryGroupId, resourceStorePath ) );
        }
        catch ( NoSuchRepositoryException e )
        {
            getResponse().setStatus( Status.CLIENT_ERROR_NOT_FOUND, e.getMessage() );
        }
        catch ( NoSuchRepositoryGroupException e )
        {
            getResponse().setStatus( Status.CLIENT_ERROR_NOT_FOUND, e.getMessage() );
        }
    }

}
