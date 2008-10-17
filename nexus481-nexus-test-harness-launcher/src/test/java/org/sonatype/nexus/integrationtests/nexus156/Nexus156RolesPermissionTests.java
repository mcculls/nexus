package org.sonatype.nexus.integrationtests.nexus156;

import java.io.IOException;

import org.testng.Assert;

import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.rest.model.RoleResource;
import org.testng.annotations.Test;

/**
 * Test the privileges for CRUD operations.
 */
public class Nexus156RolesPermissionTests
    extends AbstractPrivilegeTest
{

    @Test
    public void testCreatePermission()
        throws IOException
    {
        RoleResource role = new RoleResource();

        role.setDescription( "testCreatePermission" );
        role.setName( "testCreatePermission" );
        role.setSessionTimeout( 30 );
        role.addPrivilege( "1" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        Response response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give create
        this.giveUserPrivilege( "test-user", "34" );

        // now.... it should work...
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals( 201, response.getStatus().getCode(), "Response status: " );

        role = this.roleUtil.getResourceFromResponse( response );

        // read should succeed (inherited)
        response = this.roleUtil.sendMessage( Method.GET, role );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

        // update should fail
        response = this.roleUtil.sendMessage( Method.PUT, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // delete should fail
        response = this.roleUtil.sendMessage( Method.DELETE, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

    }

    @Test
    public void testUpdatePermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        RoleResource role = new RoleResource();
        role.setDescription( "testUpdatePermission" );
        role.setName( "testUpdatePermission" );
        role.setSessionTimeout( 30 );
        role.addPrivilege( "1" );

        Response response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals( 201, response.getStatus().getCode(), "Response status: " );
        role = this.roleUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // update user
        role.setName( "testUpdatePermission2" );
        response = this.roleUtil.sendMessage( Method.PUT, role );
        // log.debug( "PROBLEM: "+ this.userUtil.getUser( "test-user" ) );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give update
        this.giveUserPrivilege( "test-user", "36" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...

        // update user
        response = this.roleUtil.sendMessage( Method.PUT, role );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

        // read should succeed (inherited)
        response = this.roleUtil.sendMessage( Method.GET, role );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

        // update should fail
        response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // delete should fail
        response = this.roleUtil.sendMessage( Method.DELETE, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

    }

    @Test
    public void testReadPermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        RoleResource role = new RoleResource();
        role.setDescription( "testReadPermission" );
        role.setName( "testReadPermission" );
        role.setSessionTimeout( 30 );
        role.addPrivilege( "1" );

        Response response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals( 201, response.getStatus().getCode(), "Response status: " );
        role = this.roleUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.roleUtil.sendMessage( Method.PUT, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give read
        this.giveUserPrivilege( "test-user", "35" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...

        // update user
        response = this.roleUtil.sendMessage( Method.PUT, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // read should fail
        response = this.roleUtil.sendMessage( Method.GET, role );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Response status: " );

        // update should fail
        response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // delete should fail
        response = this.roleUtil.sendMessage( Method.DELETE, role );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

    }

    @Test
    public void testDeletePermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        RoleResource role = new RoleResource();
        role.setDescription( "testUpdatePermission" );
        role.setName( "testUpdatePermission" );
        role.setSessionTimeout( 30 );
        role.addPrivilege( "1" );

        Response response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals( 201, response.getStatus().getCode() , "Response status: " );
        role = this.roleUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.roleUtil.sendMessage( Method.DELETE, role );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give create
        this.giveUserPrivilege( "test-user", "37" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...

        // update user
        response = this.roleUtil.sendMessage( Method.PUT, role );
        Assert.assertEquals(  401, response.getStatus().getCode(), "Response status: " );

        // read should succeed (inherited)
        response = this.roleUtil.sendMessage( Method.GET, role );
        Assert.assertEquals(  200, response.getStatus().getCode() , "Response status: " );

        // update should fail
        response = this.roleUtil.sendMessage( Method.POST, role );
        Assert.assertEquals(  401, response.getStatus().getCode() , "Response status: " );

        // delete should fail
        response = this.roleUtil.sendMessage( Method.DELETE, role );
        Assert.assertEquals(  204, response.getStatus().getCode(), "Response status: " );

    }

}
