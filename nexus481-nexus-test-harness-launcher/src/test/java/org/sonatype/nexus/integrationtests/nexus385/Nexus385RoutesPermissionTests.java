package org.sonatype.nexus.integrationtests.nexus385;

import java.io.IOException;

import org.testng.Assert;

import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.rest.model.RepositoryRouteResource;
import org.testng.annotations.Test;

/**
 * Test the privilege for CRUD operations.
 */
public class Nexus385RoutesPermissionTests extends AbstractPrivilegeTest
{
    
    @Test
    public void testCreatePermission()
        throws IOException
    {
        RepositoryRouteResource route = new RepositoryRouteResource();
        route.setGroupId( "nexus-test" );
        route.setPattern( ".*testCreatePermission.*" );
        route.setRuleType( "blocking" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        Response response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give create
        this.giveUserPrivilege( "test-user", "22" );
        

        // now.... it should work...
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals(  201, response.getStatus().getCode() , "Response status: " );
        route = this.routeUtil.getResourceFromResponse( response );

        // read should succeed (inherited)
        response = this.routeUtil.sendMessage( Method.GET, route );
        Assert.assertEquals(  200, response.getStatus().getCode() , "Response status: " );
        
        // update should fail
        response = this.routeUtil.sendMessage( Method.PUT, route );
        Assert.assertEquals(  401, response.getStatus().getCode(), "Response status: " );
        
        // delete should fail
        response = this.routeUtil.sendMessage( Method.DELETE, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

    }

    @Test
    public void testUpdatePermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        RepositoryRouteResource route = new RepositoryRouteResource();
        route.setGroupId( "nexus-test" );
        route.setPattern( ".*testUpdatePermission.*" );
        route.setRuleType( "blocking" );

        Response response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals( 201, response.getStatus().getCode() , "Response status: " );
        route = this.routeUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // update user
        route.setPattern( ".*testUpdatePermission2.*" );
        response = this.routeUtil.sendMessage( Method.PUT, route );
//        log.debug( "PROBLEM: "+ this.userUtil.getUser( "test-user" ) );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give update
        this.giveUserPrivilege( "test-user", "24" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...
        
        // update user
        response = this.routeUtil.sendMessage( Method.PUT, route );
        Assert.assertEquals( 204, response.getStatus().getCode() , "Response status: " );

        // read should succeed (inherited)
        response = this.routeUtil.sendMessage( Method.GET, route );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );
        
        // update should fail
        response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );
        
        // delete should fail
        response = this.routeUtil.sendMessage( Method.DELETE, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );
        
        
    }
    
    @Test
    public void testReadPermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        RepositoryRouteResource route = new RepositoryRouteResource();
        route.setGroupId( "nexus-test" );
        route.setPattern( ".*testUpdatePermission.*" );
        route.setRuleType( "blocking" );

        Response response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals( 201, response.getStatus().getCode() , "Response status: " );
        route = this.routeUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.routeUtil.sendMessage( Method.PUT, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give read
        this.giveUserPrivilege( "test-user", "23" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...
        
        // update user
        response = this.routeUtil.sendMessage( Method.PUT, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // read should fail
        response = this.routeUtil.sendMessage( Method.GET, route );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );
        
        // update should fail
        response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );
        
        // delete should fail
        response = this.routeUtil.sendMessage( Method.DELETE, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );
        
        
    }
    
    @Test
    public void testDeletePermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        RepositoryRouteResource route = new RepositoryRouteResource();
        route.setGroupId( "nexus-test" );
        route.setPattern( ".*testUpdatePermission.*" );
        route.setRuleType( "blocking" );

        Response response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals( 201, response.getStatus().getCode() , "Response status: " );
        route = this.routeUtil.getResourceFromResponse( response );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );


        response = this.routeUtil.sendMessage( Method.DELETE, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().setUsername( "admin" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // now give create
        this.giveUserPrivilege( "test-user", "25" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...
        
        // update user
        response = this.routeUtil.sendMessage( Method.PUT, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // read should succeed (inherited)
        response = this.routeUtil.sendMessage( Method.GET, route );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );
        
        // update should fail
        response = this.routeUtil.sendMessage( Method.POST, route );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );
        
        // delete should fail
        response = this.routeUtil.sendMessage( Method.DELETE, route );
        Assert.assertEquals( 204, response.getStatus().getCode() , "Response status: " );
        
        
    }
    
    
}
