/**
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.rest.indextreeview;

import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.sonatype.nexus.index.IndexerManager;
import org.sonatype.nexus.index.treeview.TreeNode;
import org.sonatype.nexus.index.treeview.TreeNodeFactory;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.rest.AbstractNexusPlexusResource;
import org.sonatype.plexus.rest.resource.PlexusResourceException;

import com.thoughtworks.xstream.XStream;

/**
 * Abstract index content resource.
 *
 * @author dip
 */
public abstract class AbstractIndexContentPlexusResource
    extends AbstractNexusPlexusResource
{
    @Requirement
    protected IndexerManager indexerManager;

    @Override
    public Object getPayloadInstance()
    {
        return null;
    }
    
    @Override
    public void configureXStream( XStream xstream )
    {
        super.configureXStream( xstream );
        
        xstream.processAnnotations( IndexBrowserTreeViewResponseDTO.class );
    }

    @Override
    public Object get( Context context, Request request, Response response, Variant variant )
        throws ResourceException
    {
        String path = parsePathFromUri( request.getResourceRef().toString() );
        if ( !path.endsWith( "/" ) )
        {
            response.redirectPermanent( path + "/" );
            return null;
        }
        
        try
        {
            Repository repository = getRepositoryRegistry().getRepository( getRepositoryId( request ) );
            
            TreeNodeFactory factory = new IndexBrowserTreeNodeFactory( 
                indexerManager.getRepositoryBestIndexContext( repository.getId() ), 
                repository, 
                createRedirectBaseRef( request ).toString() );
            
            TreeNode node = indexerManager.listNodes( factory, repository, path );
            
            if ( node == null )
            {
                throw new PlexusResourceException( Status.SERVER_ERROR_INTERNAL, "Unable to retrieve index tree nodes" );
            }
            
            return new IndexBrowserTreeViewResponseDTO( node );
        }
        catch ( NoSuchRepositoryException e )
        {
            getLogger().error( "Invalid repository retrieved", e );
            throw new PlexusResourceException( Status.SERVER_ERROR_INTERNAL, "Invalid repository retrieved", e );
        }
    }
    
    protected abstract String getRepositoryId( Request request );

    protected String parsePathFromUri( String parsedPath )
    {
        // get rid of query part
        if ( parsedPath.contains( "?" ) )
        {
            parsedPath = parsedPath.substring( 0, parsedPath.indexOf( '?' ) );
        }

        // get rid of reference part
        if ( parsedPath.contains( "#" ) )
        {
            parsedPath = parsedPath.substring( 0, parsedPath.indexOf( '#' ) );
        }

        if ( StringUtils.isEmpty( parsedPath ) )
        {
            parsedPath = "/";
        }
        
        int index = parsedPath.indexOf( "index_content" );
        
        if ( index > -1 )
        {
            parsedPath = parsedPath.substring( index + "index_content".length() );
        }

        return parsedPath;
    }
}
