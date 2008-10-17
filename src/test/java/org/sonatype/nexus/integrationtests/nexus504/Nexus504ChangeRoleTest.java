package org.sonatype.nexus.integrationtests.nexus504;

import org.testng.Assert;

import org.aspectj.lang.annotation.Before;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.integrationtests.TestContext;
import org.sonatype.nexus.integrationtests.nexus450.UserCreationUtil;
import org.sonatype.nexus.rest.model.RoleResource;
import org.sonatype.nexus.test.utils.RoleMessageUtil;
import org.sonatype.nexus.test.utils.UserMessageUtil;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.thoughtworks.xstream.XStream;

/**
 * Created a role without the Login to UI privilege => Created a user and associated the role to that user => After the
 * user was created, I edited the role associated to that user and added the Login to UI privilege => Note that the user
 * was still not able to log in. However, all new users I created associated to that role had the ability to log in.
 */
public class Nexus504ChangeRoleTest
    extends AbstractPrivilegeTest
{

    private static final String NEXUS504_USER = "nexus504-user";

    private static final String NEXUS504_ROLE = "nexus504-role";

    private RoleMessageUtil roleUtil;

    @BeforeTest
    public void init()
    {
        XStream xstream = this.getXMLXStream();

        this.userUtil = new UserMessageUtil( xstream, MediaType.APPLICATION_XML );
        this.roleUtil = new RoleMessageUtil( xstream, MediaType.APPLICATION_XML );
    }

    @Test
    public void test()
        throws Exception
    {
        // use admin
        TestContext testContext = TestContainer.getInstance().getTestContext();

        // user is created at security.xml

        testContext.setUsername( NEXUS504_USER );
        testContext.setPassword( TEST_USER_PASSWORD );

        Status status = UserCreationUtil.login();
        Assert.assertEquals( 401, status.getCode(), "User should not be able to login " );

        // add login privilege to role
        testContext.useAdminForRequests();

        RoleResource role = roleUtil.getRole( NEXUS504_ROLE );
        role.addPrivilege( "2"/* login */);
        status = RoleMessageUtil.update( role );
        Assert.assertTrue( status.isSuccess(), "Unable to add login privilege to role " + NEXUS504_ROLE + "\n" + status.getDescription() );

        // try to login again
        testContext.setUsername( NEXUS504_USER );
        testContext.setPassword( TEST_USER_PASSWORD );
        status = UserCreationUtil.login();
        Assert.assertEquals( 200, status.getCode(), "User should be able to login " );
    }
}
