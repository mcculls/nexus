package org.sonatype.nexus.restlight.core.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.Role;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

import java.util.List;

public class ListRolesIT
    extends AbstractRestlightIT
{

    @Test
    public void listRole()
        throws Exception
    {
        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        List<Role> roles = client.listRole();

        System.out.println( StringUtils.join( roles.iterator(), "\n\n" ) );

        assertNotNull( roles );

        String anonRoleResourceUri = getBaseNexusUrl() + "service/local/roles/anonymous";

        boolean found = false;
        for ( Role role : roles )
        {
            if ( anonRoleResourceUri.equals( role.getResourceURI() ) )
            {
                found = true;

                assertEquals( "anonymous", role.getId() );
                assertEquals( "Nexus Anonymous Role", role.getName() );
                assertEquals( "Anonymous role for Nexus", role.getDescription() );
                assertEquals( 60, role.getSessionTimeout() );
                assertEquals( false, role.isUserManaged() );

                assertNotNull( role.getRoles() );

                assertTrue( "should have 2 or more sub-roles; had " + role.getRoles().size(),
                            role.getRoles().size() >= 2 );

                assertTrue( "ui-repo-browser missing", role.getRoles().contains( "ui-repo-browser" ) );
                assertTrue( "ui-search missing", role.getRoles().contains( "ui-search" ) );

                assertNotNull( role.getPrivileges() );

                assertTrue( "should have 6 or more privileges; had " + role.getPrivileges().size(),
                            role.getPrivileges().size() >= 6 );

                assertTrue( "1 missing", role.getPrivileges().contains( "1" ) );
                assertTrue( "54 missing", role.getPrivileges().contains( "54" ) );
                assertTrue( "57 missing", role.getPrivileges().contains( "57" ) );
                assertTrue( "58 missing", role.getPrivileges().contains( "58" ) );
                assertTrue( "70 missing", role.getPrivileges().contains( "70" ) );
                assertTrue( "74 missing", role.getPrivileges().contains( "74" ) );

                break;
            }
        }

        assertTrue( "anonymous role not found", found );
    }
}
