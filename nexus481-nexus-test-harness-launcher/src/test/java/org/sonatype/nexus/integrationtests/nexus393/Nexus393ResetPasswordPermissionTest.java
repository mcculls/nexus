package org.sonatype.nexus.integrationtests.nexus393;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.TestContainer;

/**
 * Test the privilege for password reset.
 */
public class Nexus393ResetPasswordPermissionTest
    extends AbstractPrivilegeTest
{

    @Test
    public void resetWithPermission()
        throws Exception
    {
        overwriteUserRole( TEST_USER_NAME, "anonymous-with-login-reset", "1", "2" /* login */, "6", "14", "17", "19",
                           "44", "54", "55", "56", "57", "58", "59"/* reset */, "T1", "T2" );

        TestContainer.getInstance().getTestContext().setUsername( TEST_USER_NAME );
        TestContainer.getInstance().getTestContext().setPassword( TEST_USER_PASSWORD );

        // Should be able to reset anyone password
        String username = "another-user";
        Response response = ResetPasswordUtils.resetPassword( username );
        Assert.assertTrue( response.getStatus().isSuccess(), "Status: "+ response.getStatus() );

        // Should be able to reset my own password
        username = TEST_USER_NAME;
        response = ResetPasswordUtils.resetPassword( username );
        Assert.assertTrue( response.getStatus().isSuccess(),"Status: "+ response.getStatus() );

    }

    @Test
    public void resetWithoutPermission()
        throws Exception
    {
        overwriteUserRole( TEST_USER_NAME, "anonymous-with-login-but-reset", "1", "2" /* login */, "6", "14", "17",
                           "19", "44", "54", "55", "56", "57", "58", /* "59" reset , */"T1", "T2" );

        TestContainer.getInstance().getTestContext().setUsername( TEST_USER_NAME );
        TestContainer.getInstance().getTestContext().setPassword( TEST_USER_PASSWORD );

        // NOT Shouldn't be able to reset anyone password
        String username = "another-user";
        Response response = ResetPasswordUtils.resetPassword( username );
        Assert.assertEquals(401, response.getStatus().getCode(),"Status: "+ response.getStatus() +"\n"+ response.getEntity().getText() );

        // NOT Should be able to reset my own password
        username = TEST_USER_NAME;
        response = ResetPasswordUtils.resetPassword( username );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Status: "+ response.getStatus() +"\n"+ response.getEntity().getText() );

    }
}
