package org.sonatype.nexus.restlight.core.it;

import org.junit.Ignore;
import org.junit.Test;
import org.sonatype.nexus.restlight.core.CoreClient;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;

public class DeleteUserIT
    extends AbstractRestlightIT
{

    // @BeforeClass
    // public static void setSecure()
    // {
    // TestContainer.getInstance().getTestContext().setSecureTest( true );
    // }

    @Test
    @Ignore( "Returns 405 HTTP status from Nexus 1.4.0" )
    public void deleteUser()
        throws Exception
    {
        String userId = "user-test";

        CoreClient client = new CoreClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        client.deleteUser( userId );
    }
}
