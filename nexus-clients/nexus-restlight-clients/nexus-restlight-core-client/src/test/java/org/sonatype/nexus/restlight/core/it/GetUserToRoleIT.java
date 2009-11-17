package org.sonatype.nexus.restlight.core.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.UserToRole;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

import java.util.Arrays;

public class GetUserToRoleIT
    extends AbstractRestlightIT
{

    @Test
    @Ignore( "[jdcasey] I don't know what 'source' means in this context." )
    public void getUserToRole()
        throws Exception
    {
        String source = "url";
        String userId = "deployment";

        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );
        UserToRole result = client.getUserToRole( userId, source );

        assertNotNull( result );
        assertEquals( "url", result.getSource() );
        assertEquals( userId, result.getUserId() );
        String[] roles = { "ui-basic", "ui-logs-config-files" };
        assertEquals( Arrays.asList( roles ), result.getRoles() );
    }
}
