package org.sonatype.nexus.integrationtests.nexus408;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.TestContainer;

public class Nexus408ChangePasswordTest
    extends AbstractNexusIntegrationTest
{

    public Nexus408ChangePasswordTest() {
        TestContainer.getInstance().getTestContext().setSecureTest( true );
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );
    }

    @Test
    public void changeUserPassword()
        throws Exception
    {
        Response response = ChangePasswordUtils.recoverUsername( "admin", "admin123", "123admin" );
        Status status = response.getStatus();
        Assert.assertEquals( Status.SUCCESS_OK.getCode(), status.getCode() );
    }

}
