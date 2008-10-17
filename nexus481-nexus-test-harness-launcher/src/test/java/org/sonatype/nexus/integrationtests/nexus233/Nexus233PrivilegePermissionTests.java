package org.sonatype.nexus.integrationtests.nexus233;

import java.io.IOException;

import org.testng.Assert;

import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.rest.model.PrivilegeBaseStatusResource;
import org.sonatype.nexus.rest.model.PrivilegeTargetResource;
import org.testng.annotations.Test;

/**
 * Test the privileges for CRUD operations.
 */
public class Nexus233PrivilegePermissionTests
    extends AbstractPrivilegeTest
{

    @Test
    public void testCreatePermission()
        throws IOException
    {
        PrivilegeTargetResource privilege = new PrivilegeTargetResource();
        privilege.addMethod( "read" );
        privilege.setName( "createReadMethodTest" );
        privilege.setType( "repositoryTarget" );
        privilege.setRepositoryTargetId( "testTarget" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        Response response = this.privUtil.sendMessage( Method.POST, privilege );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give create
        this.giveUserPrivilege( "test-user", "30" );

        // now.... it should work...
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.privUtil.sendMessage( Method.POST, privilege );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );
        PrivilegeBaseStatusResource responsePrivilege = this.privUtil.getResourceFromResponse( response );

        // read should succeed (inherited by create)
        response = this.privUtil.sendMessage( Method.GET, null, responsePrivilege.getId() );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

        // update should fail
        response = this.privUtil.sendMessage( Method.PUT, privilege, responsePrivilege.getId() );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // delete should fail
        response = this.privUtil.sendMessage( Method.DELETE, null, responsePrivilege.getId() );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

    }

    @Test
    public void testReadPermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        PrivilegeTargetResource privilege = new PrivilegeTargetResource();
        privilege.addMethod( "read" );
        privilege.setName( "createReadMethodTest" );
        privilege.setType( "repositoryTarget" );
        privilege.setRepositoryTargetId( "testTarget" );

        Response response = this.privUtil.sendMessage( Method.POST, privilege );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );
        PrivilegeBaseStatusResource responsePrivilege = this.privUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.privUtil.sendMessage( Method.GET, null, responsePrivilege.getId() );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give create
        this.giveUserPrivilege( "test-user", "31" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...
        response = this.privUtil.sendMessage( Method.PUT, privilege, responsePrivilege.getId() );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

        // read should fail
        response = this.privUtil.sendMessage( Method.GET, null, responsePrivilege.getId() );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // update should fail
        response = this.privUtil.sendMessage( Method.POST, privilege );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // delete should fail
        response = this.privUtil.sendMessage( Method.DELETE, null, responsePrivilege.getId() );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

    }

    @Test
    public void testDeletePermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        PrivilegeTargetResource privilege = new PrivilegeTargetResource();
        privilege.addMethod( "read" );
        privilege.setName( "createReadMethodTest" );
        privilege.setType( "repositoryTarget" );
        privilege.setRepositoryTargetId( "testTarget" );

        Response response = this.privUtil.sendMessage( Method.POST, privilege );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );
        PrivilegeBaseStatusResource responsePrivilege = this.privUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.privUtil.sendMessage( Method.DELETE, null, responsePrivilege.getId() );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give delete
        this.giveUserPrivilege( "test-user", "33" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...
        response = this.privUtil.sendMessage( Method.PUT, privilege, responsePrivilege.getId() );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // read should succeed (inherited by delete)
        response = this.privUtil.sendMessage( Method.GET, null, responsePrivilege.getId() );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

        // update should fail
        response = this.privUtil.sendMessage( Method.POST, privilege );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // delete should fail
        response = this.privUtil.sendMessage( Method.DELETE, null, responsePrivilege.getId() );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

    }
}
