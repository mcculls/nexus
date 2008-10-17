package org.sonatype.nexus.integrationtests.nexus532;

import java.io.IOException;

import org.testng.Assert;

import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.rest.model.RepositoryGroupMemberRepository;
import org.sonatype.nexus.rest.model.RepositoryGroupResource;
import org.testng.annotations.Test;

/**
 * Test Group CRUD privileges.
 */
public class Nexus532GroupCrudPermissionTests
    extends AbstractPrivilegeTest
{

    @Test
    public void testCreatePermission()
        throws IOException
    {
        RepositoryGroupResource group = new RepositoryGroupResource();
        group.setId( "testCreatePermission" );
        group.setName( "testCreatePermission" );
        group.setFormat( "maven2" );

        RepositoryGroupMemberRepository member = new RepositoryGroupMemberRepository();
        member.setId( "nexus-test-harness-repo" );
        group.addRepository( member );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        Response response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 401, response.getStatus().getCode() );

        // use admin
        TestContainer.getInstance().getTestContext().useAdminForRequests();

        // now give create
        this.giveUserPrivilege( "test-user", "13" );

        // now.... it should work...
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 201, response.getStatus().getCode(), "Response status: " );
        group = this.groupUtil.getGroup( group.getId() );

        // read should succeed (inherited)
        response = this.groupUtil.sendMessage( Method.GET, group );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );

        // update should fail
        response = this.groupUtil.sendMessage( Method.PUT, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // delete should fail
        response = this.groupUtil.sendMessage( Method.DELETE, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

    }

    @Test
    public void testUpdatePermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().useAdminForRequests();

        RepositoryGroupResource group = new RepositoryGroupResource();
        group.setId( "testUpdatePermission" );
        group.setName( "testUpdatePermission" );
        group.setFormat( "maven2" );

        RepositoryGroupMemberRepository member = new RepositoryGroupMemberRepository();
        member.setId( "nexus-test-harness-repo" );
        group.addRepository( member );

        Response response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 201, response.getStatus().getCode() , "Response status: " );
        group = this.groupUtil.getGroup( group.getId() );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // update repo
        group.setName( "tesUpdatePermission2" );
        response = this.groupUtil.sendMessage( Method.PUT, group );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().useAdminForRequests();

        // now give update
        this.giveUserPrivilege( "test-user", "15" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // should work now...
        response = this.groupUtil.sendMessage( Method.PUT, group );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );

        // read should succeed (inherited)
        response = this.groupUtil.sendMessage( Method.GET, group );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );

        // update should fail
        response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // delete should fail
        response = this.groupUtil.sendMessage( Method.DELETE, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

    }

    @Test
    public void testReadPermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().useAdminForRequests();

        RepositoryGroupResource group = new RepositoryGroupResource();
        group.setId( "testReadPermission" );
        group.setName( "testReadPermission" );
        group.setFormat( "maven2" );

        RepositoryGroupMemberRepository member = new RepositoryGroupMemberRepository();
        member.setId( "nexus-test-harness-repo" );
        group.addRepository( member );

        Response response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 201, response.getStatus().getCode() , "Response status: " );
        group = this.groupUtil.getGroup( group.getId() );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // update repo
        group.setName( "tesUpdatePermission2" );
        response = this.groupUtil.sendMessage( Method.PUT, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().useAdminForRequests();

        // now give read
        this.giveUserPrivilege( "test-user", "14" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // read should fail
        response = this.groupUtil.sendMessage( Method.GET, group );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );

        // update should fail
        response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // delete should fail
        response = this.groupUtil.sendMessage( Method.PUT, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // should work now...
        response = this.groupUtil.sendMessage( Method.DELETE, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

    }

    @Test
    public void testDeletePermission()
        throws IOException
    {

        TestContainer.getInstance().getTestContext().useAdminForRequests();

        RepositoryGroupResource group = new RepositoryGroupResource();
        group.setId( "testDeletePermission" );
        group.setName( "testDeletePermission" );
        group.setFormat( "maven2" );

        RepositoryGroupMemberRepository member = new RepositoryGroupMemberRepository();
        member.setId( "nexus-test-harness-repo" );
        group.addRepository( member );

        Response response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 201, response.getStatus().getCode() , "Response status: " );
        group = this.groupUtil.getGroup( group.getId() );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // update repo
        group.setName( "tesUpdatePermission2" );
        response = this.groupUtil.sendMessage( Method.DELETE, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // use admin
        TestContainer.getInstance().getTestContext().useAdminForRequests();

        // now give delete
        this.giveUserPrivilege( "test-user", "16" );

        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        // read should succeed (inherited)
        response = this.groupUtil.sendMessage( Method.GET, group );
        Assert.assertEquals( 200, response.getStatus().getCode() , "Response status: " );

        // update should fail
        response = this.groupUtil.sendMessage( Method.POST, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // delete should fail
        response = this.groupUtil.sendMessage( Method.PUT, group );
        Assert.assertEquals( 401, response.getStatus().getCode() , "Response status: " );

        // should work now...
        response = this.groupUtil.sendMessage( Method.DELETE, group );
        Assert.assertEquals( 204, response.getStatus().getCode() , "Response status: " );

    }

}
