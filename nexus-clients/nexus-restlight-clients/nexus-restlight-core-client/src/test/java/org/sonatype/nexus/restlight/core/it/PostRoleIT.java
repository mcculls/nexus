package org.sonatype.nexus.restlight.core.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.Role;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

public class PostRoleIT
    extends AbstractRestlightIT
{

    @Test
    public void postRole()
        throws Exception
    {
        Role role = new Role();
        role.setId( "a1" );
        role.setName( "a11" );
        role.setDescription( "a111" );
        role.setSessionTimeout( 100 );
        role.getRoles().add( "anonymous" );
        role.getPrivileges().add( "18" );

        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        Role roleResp = client.postRole( role );

        assertNotNull( roleResp );
        assertEquals( getBaseNexusUrl() + "service/local/roles/a1", roleResp.getResourceURI() );
        assertEquals( "a1", roleResp.getId() );
        assertEquals( "a11", roleResp.getName() );
        assertEquals( "a111", roleResp.getDescription() );
        assertEquals( 100, roleResp.getSessionTimeout() );
        assertEquals( true, roleResp.isUserManaged() );
        assertEquals( 1, roleResp.getRoles().size() );
        assertEquals( "anonymous", roleResp.getRoles().get( 0 ) );
        assertEquals( 1, roleResp.getPrivileges().size() );
        assertEquals( "18", roleResp.getPrivileges().get( 0 ) );
    }
}
