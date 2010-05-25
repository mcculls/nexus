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
package org.sonatype.nexus.security.filter.authz;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.sonatype.nexus.Nexus;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.feeds.AuthcAuthzEvent;
import org.sonatype.nexus.feeds.FeedRecorder;
import org.sonatype.nexus.proxy.access.AccessManager;
import org.sonatype.nexus.rest.RemoteIPFinder;
import org.sonatype.nexus.web.NexusBooterListener;
import org.sonatype.security.web.filter.authz.HttpVerbMappingAuthorizationFilter;

/**
 * A filter that maps the action from the HTTP Verb.
 * 
 * @author cstamas
 */
public class FailureReportingAuthorizationFilter
    extends HttpVerbMappingAuthorizationFilter
{

    public static final String REQUEST_IS_AUTHZ_REJECTED = "request.is.authz.rejected";

    private AuthcAuthzEvent currentAuthzEvt;

    protected Nexus getNexus( ServletRequest request )
    {
        Nexus nexus = (Nexus) request.getAttribute( Nexus.class.getName() );

        if ( nexus == null )
        {
            nexus = NexusBooterListener.getNexus();
        }

        return nexus;
    }

    protected PlexusContainer getPlexusContainer()
    {
        return (PlexusContainer) getAttribute( PlexusConstants.PLEXUS_KEY );
    }

    protected NexusConfiguration getNexusConfiguration()
    {
        return (NexusConfiguration) getAttribute( NexusConfiguration.class.getName() );
    }

    @Override
    protected boolean onAccessDenied( ServletRequest request, ServletResponse response )
        throws IOException
    {
        recordAuthzFailureEvent( request, response );

        request.setAttribute( REQUEST_IS_AUTHZ_REJECTED, Boolean.TRUE );

        return super.onAccessDenied( request, response );
    }

    private void recordAuthzFailureEvent( ServletRequest request, ServletResponse response )
    {
        Subject subject = getSubject( request, response );

        if ( getNexusConfiguration().getAnonymousUsername().equals( subject.getPrincipal() ) )
        {
            return;
        }

        String msg =
            "Unable to authorize user [" + subject.getPrincipal() + "] for " + getActionFromHttpVerb( request )
                + " to " + ( (HttpServletRequest) request ).getRequestURI() + " from IP Address "
                + RemoteIPFinder.findIP( (HttpServletRequest) request );

        if ( isSimilarEvent( msg ) )
        {
            return;
        }

        getLogger().info( msg );

        AuthcAuthzEvent authzEvt = new AuthcAuthzEvent( FeedRecorder.SYSTEM_AUTHZ, msg );

        if ( HttpServletRequest.class.isAssignableFrom( request.getClass() ) )
        {
            String ip = RemoteIPFinder.findIP( (HttpServletRequest) request );

            if ( ip != null )
            {
                authzEvt.getEventContext().put( AccessManager.REQUEST_REMOTE_ADDRESS, ip );
            }
        }

        Nexus nexus = getNexus( request );
        if ( nexus != null )
        {
            nexus.addAuthcAuthzEvent( authzEvt );
        }

        currentAuthzEvt = authzEvt;
    }

    private boolean isSimilarEvent( String msg )
    {
        if ( currentAuthzEvt == null )
        {
            return false;
        }

        if ( currentAuthzEvt.getMessage().equals( msg )
            && ( System.currentTimeMillis() - currentAuthzEvt.getEventDate().getTime() < 2000L ) )
        {
            return true;
        }

        return false;
    }
    
    protected Object getAttribute( String key )
    {
        return this.getFilterConfig().getServletContext().getAttribute( key );
    }
}
