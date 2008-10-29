package org.sonatype.nexus.integrationtests.nexus999;

import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.test.utils.ChangePasswordUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Nexus999SetUsersPassword
    extends AbstractPrivilegeTest
{

    @Test
    public void changePassword()
        throws Exception
    {

        Status status = ChangePasswordUtils.changePassword( "test-user", "newPassword" );
        Assert.assertEquals( 204, status.getCode(), "Status" );

        // we need to change the password around for this
        status = ChangePasswordUtils.changePassword( "test-user", TEST_USER_PASSWORD );
        Assert.assertEquals( 204, status.getCode(), "Status" );
    }

    @Test
    public void withPermission()
        throws Exception
    {
        overwriteUserRole(
            TEST_USER_NAME,
            "anonymous-with-login-setpw",
            "1",
            "2" /* login */,
            "6",
            "14",
            "17",
            "19",
            "44",
            "54",
            "55",
            "56",
            "57",
            "58",
            "59",
            "72"/* set pw */,
            "T1",
            "T2" );

        TestContainer.getInstance().getTestContext().setUsername( TEST_USER_NAME );
        TestContainer.getInstance().getTestContext().setPassword( TEST_USER_PASSWORD );

        // Should be able to change my own password
        Status status = ChangePasswordUtils.changePassword( "test-user", "newPassword" );
        Assert.assertEquals( 204, status.getCode(), "Status" );

        // we need to change the password around for this
        TestContainer.getInstance().getTestContext().setPassword( "newPassword" );
        status = ChangePasswordUtils.changePassword( "test-user", "newPassword" );
        Assert.assertEquals( 204, status.getCode(), "Status" );

        status = ChangePasswordUtils.changePassword( "test-user", TEST_USER_PASSWORD );
        Assert.assertEquals( 204, status.getCode(), "Status" );
        TestContainer.getInstance().getTestContext().setPassword( TEST_USER_PASSWORD );
    }

    @Test
    public void withoutPermission()
        throws Exception
    {
        overwriteUserRole(
            TEST_USER_NAME,
            "anonymous-with-login-but-setpw",
            "1",
            "2" /* login */,
            "6",
            "14",
            "17",
            "19",
            "44",
            "54",
            "55",
            "56",
            "57",
            "58",
            "59", /* "72" set pw, */
            "T1",
            "T2" );

        TestContainer.getInstance().getTestContext().setUsername( TEST_USER_NAME );
        TestContainer.getInstance().getTestContext().setPassword( TEST_USER_PASSWORD );

        // NOT Should be able to forgot my own username
        Status status = ChangePasswordUtils.changePassword( "test-user", "123admin" );
        Assert.assertEquals( 401, status.getCode() );

        // NOT Should be able to forgot anyone username
        status = ChangePasswordUtils.changePassword( "admin", "123admin" );
        Assert.assertEquals( 401, status.getCode() );
    }

}
