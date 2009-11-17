package org.sonatype.nexus.restlight.core.it;

import org.junit.Ignore;
import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.core.UserToRole;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

public class PutUserToRoleIT
    extends AbstractRestlightIT
{

    // @BeforeClass
    // public static void setSecure()
    // {
    // TestContainer.getInstance().getTestContext().setSecureTest( true );
    // }

    @Test
    @Ignore( "Returns 405 HTTP status from Nexus 1.4.0" )
    public void putUserToRole()
        throws Exception
    {
        String source = "url";
        String userId = "deployment";

        UserToRole userToRole = new UserToRole();
        userToRole.setUserId( userId );
        userToRole.setSource( source );
        userToRole.getRoles().add( "anonymous" );
        userToRole.getRoles().add( "developer" );

        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );
        client.putUserToRole( userToRole );
    }
}
