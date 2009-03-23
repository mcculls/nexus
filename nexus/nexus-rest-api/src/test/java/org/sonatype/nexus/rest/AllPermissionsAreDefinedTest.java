package org.sonatype.nexus.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.jsecurity.model.CPrivilege;
import org.sonatype.jsecurity.model.CProperty;
import org.sonatype.jsecurity.model.Configuration;
import org.sonatype.jsecurity.model.io.xpp3.SecurityConfigurationXpp3Reader;
import org.sonatype.jsecurity.realms.tools.dao.SecurityPrivilege;
import org.sonatype.nexus.AbstractNexusTestCase;
import org.sonatype.nexus.jsecurity.NexusSecurity;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;

public class AllPermissionsAreDefinedTest
    extends AbstractNexusTestCase
{

    private List<PlexusResource> getPlexusResources()
        throws ComponentLookupException
    {
        return this.getContainer().lookupList( PlexusResource.class );
    }

    private NexusSecurity getSecurity()
        throws Exception
    {
        return this.lookup( NexusSecurity.class );
    }

    @SuppressWarnings( "unchecked" )
    public void testEnsurePermissions()
        throws Exception
    {
        Set<String> restPerms = new HashSet<String>();
        Set<String> staticPerms = new HashSet<String>();

        Map<String, List<PlexusResource>> debugMap = new HashMap<String, List<PlexusResource>>();

        for ( PlexusResource plexusResource : this.getPlexusResources() )
        {
            PathProtectionDescriptor ppd = plexusResource.getResourceProtection();

            String expression = ppd.getFilterExpression();
            if ( expression.contains( "[" ) )
            {
                String permission = ppd.getFilterExpression().substring(
                    expression.indexOf( '[' ) + 1,
                    expression.indexOf( ']' ) );
                restPerms.add( permission );

                this.addToDebugMap( plexusResource, permission, debugMap );
            }
        }

        // now we have a list of permissions, we need to make sure all of these are defined
        List<SecurityPrivilege> privs = this.getSecurity().listPrivileges();
        for ( CPrivilege privilege : privs )
        {
            staticPerms.add( this.getPermssionFromPrivilege( privilege ) );
        }

        List<String> errors = new ArrayList<String>();
        // make sure everything in the restPerms is in the staticPerms
        for ( String perm : restPerms )
        {
            if ( !staticPerms.contains( perm ) )
            {
                errors.add( this.getDebugMessage( perm, debugMap ) );
            }
        }

        if ( !errors.isEmpty() )
        {
            StringBuffer buffer = new StringBuffer();
            for ( String error : errors )
            {
                buffer.append( error ).append( "\n" );
            }
            Assert.fail( "Found permissions that are not defined in the security configuration:\n" + buffer.toString() );
        }

    }

    private String getPermssionFromPrivilege( CPrivilege privilege )
    {
        for ( Iterator<CProperty> iter = privilege.getProperties().iterator(); iter.hasNext(); )
        {
            CProperty prop = iter.next();
            if ( prop.getKey().equals( "permission" ) )
            {
                return prop.getValue();
            }
        }
        return null;
    }

    private void addToDebugMap( PlexusResource resource, String permission, Map<String, List<PlexusResource>> debugMap )
    {
        if ( debugMap.containsKey( permission ) )
        {
            debugMap.get( permission ).add( resource );
        }
        else
        {
            List<PlexusResource> list = new ArrayList<PlexusResource>();
            list.add( resource );
            debugMap.put( permission, list );
        }
    }

    private String getDebugMessage( String permission, Map<String, List<PlexusResource>> debugMap )
    {
        if ( debugMap.containsKey( permission ) )
        {
            return "Permission: "+ permission +" Resources: " + debugMap.get( permission );
        }
        else
        {
            return "Resource not found for permission: " + permission;
        }
    }
}
