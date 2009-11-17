package org.sonatype.nexus.restlight.core.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.PlexusUser;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

public class GetPlexusUserIT
    extends AbstractRestlightIT
{
    
    @Test
    public void getPlexusUser()
        throws Exception
    {
        final String userId = "deployment";

        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        PlexusUser plexusUser = client.getPlexusUser( userId );

        assertNotNull( plexusUser );

        assertEquals( "deployment", plexusUser.getUserId() );
        assertEquals( "Deployment User", plexusUser.getName() );
        assertEquals( "changeme1@yourcompany.com", plexusUser.getEmail() );
        assertEquals( "default", plexusUser.getSource() );

        assertEquals( 2, plexusUser.getPlexusRoles().size() );
        assertEquals( "repo-all-full", plexusUser.getPlexusRoles().get( 0 ).getRoleId() );
        assertEquals( "Repo: All Repositories (Full Control)", plexusUser.getPlexusRoles().get( 0 ).getName() );
        assertEquals( "default", plexusUser.getPlexusRoles().get( 0 ).getSource() );
        assertEquals( "deployment", plexusUser.getPlexusRoles().get( 1 ).getRoleId() );
        assertEquals( "Nexus Deployment Role", plexusUser.getPlexusRoles().get( 1 ).getName() );
        assertEquals( "default", plexusUser.getPlexusRoles().get( 1 ).getSource() );
    }
}
