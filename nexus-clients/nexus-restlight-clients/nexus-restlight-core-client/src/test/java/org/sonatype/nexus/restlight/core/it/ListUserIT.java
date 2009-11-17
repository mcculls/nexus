package org.sonatype.nexus.restlight.core.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.User;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

import java.util.List;

public class ListUserIT
    extends AbstractRestlightIT
{

    @Test
    public void listUser()
        throws Exception
    {
        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        List<User> users = client.listUser();

        assertNotNull( users );

        assertEquals( 4, users.size() );
        boolean found = true;
        for ( User user : users )
        {
            if ( "admin".equals( user.getUserId() ) )
            {
                found = true;
                assertEquals( getBaseNexusUrl() + "service/local/users/admin", user.getResourceURI() );
                assertEquals( "admin", user.getUserId() );
                assertEquals( "Administrator", user.getName() );
                assertEquals( "active", user.getStatus() );
                assertEquals( "changeme@yourcompany.com", user.getEmail() );
                assertEquals( true, user.isUserManaged() );

                break;
            }
        }

        assertTrue( "Admin user not found", found );
    }
}
