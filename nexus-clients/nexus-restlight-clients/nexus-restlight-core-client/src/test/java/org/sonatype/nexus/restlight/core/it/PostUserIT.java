package org.sonatype.nexus.restlight.core.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.User;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

public class PostUserIT
    extends AbstractRestlightIT
{

    @Test
    public void postUser()
        throws Exception
    {
        User user = new User();
        user.setUserId( "bbb" );
        user.setName( "bbb" );
        user.setStatus( "active" );
        user.setEmail( "b@b.b" );
        user.setUserManaged( true );
        user.getRoles().add( "admin" );

        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        User userResp = client.postUser( user );

        assertEquals( getBaseNexusUrl() + "service/local/users/bbb", userResp.getResourceURI() );
        assertEquals( "bbb", userResp.getUserId() );
        assertEquals( "bbb", userResp.getName() );
        assertEquals( "active", userResp.getStatus() );
        assertEquals( "b@b.b", userResp.getEmail() );
        assertEquals( true, user.isUserManaged() );

        assertEquals( 1, user.getRoles().size() );
        assertTrue( "missing admin role", userResp.getRoles().contains( "admin" ) );
    }
}
