package org.sonatype.nexus.restlight.core.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.User;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

public class GetUserIT
    extends AbstractRestlightIT
{

    @Test
    public void getUser()
        throws Exception
    {
        final String userId = "deployment";

        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        User user = client.getUser( userId );

        assertNotNull( user );

        assertEquals( getBaseNexusUrl() + "service/local/users/deployment/", user.getResourceURI() );
        assertEquals( "deployment", user.getUserId() );
        assertEquals( "Deployment User", user.getName() );
        assertEquals( "active", user.getStatus() );
        assertEquals( "changeme1@yourcompany.com", user.getEmail() );
        assertEquals( true, user.isUserManaged() );
        assertEquals( 2, user.getRoles().size() );
        assertTrue( "missing deployment role", user.getRoles().contains( "deployment" ) );
        assertTrue( "missing repo-all-full role", user.getRoles().contains( "repo-all-full" ) );
    }
}
