package org.sonatype.nexus.integrationtests.nexus477;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import org.testng.Assert;

import org.apache.http.HttpException;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.aspectj.lang.annotation.Before;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.integrationtests.AbstractPrivilegeTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.test.utils.DeployUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test the privilege for CRUD operations.
 */
public class Nexus477ArtifactsCrudTests
    extends AbstractPrivilegeTest
{

    @BeforeTest
    public void deployArtifact()
        throws Exception
    {
        Gav gav =
            new Gav( this.getTestId(), "artifact", "1.0.0", null, "xml", 0, new Date().getTime(), "", false, false,
                     null, false, null );

        // Grab File used to deploy
        File fileToDeploy = this.getTestFile( gav.getArtifactId() + "." + gav.getExtension() );

        // URLConnection.set

        // use the test-user
        // this.giveUserPrivilege( "test-user", "T3" ); // the Wagon does a PUT not a POST, so this is correct
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );
        this.resetTestUserPrivs();

        int status = DeployUtils.deployUsingGavWithRest( this.getTestRepositoryId(), gav, fileToDeploy );
        Assert.assertEquals( 201, status, "Status" );
    }

    // @Test
    // public void testPost()
    // {
    // // the Wagon deploys using the PUT method
    // }

    // @Test
    // public void testPut()
    // {
    // // This is covered in Nexus429WagonDeployPrivilegeTest.
    // }

    @Test
    public void deleteTest()
        throws IOException
    {
        Gav gav =
            new Gav( this.getTestId(), "artifact", "1.0.0", null, "xml", 0, new Date().getTime(), "", false, false,
                     null, false, null );
        
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        String serviceURI =
        // "service/local/repositories/" + this.getTestRepositoryId() + "/content/" + this.getTestId() + "/";
            "content/repositories/" + this.getTestRepositoryId() + "/" + this.getTestId();

         Response response = RequestFacade.sendMessage( serviceURI, Method.DELETE );
         Assert.assertEquals( 401, response.getStatus().getCode(), "Artifact should not have been deleted" );

        TestContainer.getInstance().getTestContext().useAdminForRequests();
        this.giveUserPrivilege( "test-user", "T7" );
        
        // delete implies read
        // we need to check read first...
        response = RequestFacade.sendMessage( "content/repositories/" + this.getTestRepositoryId() + "/" + this.getRelitiveArtifactPath( gav ), Method.GET );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Could not get artifact" );

         response = RequestFacade.sendMessage( serviceURI, Method.DELETE );
         Assert.assertEquals( 204, response.getStatus().getCode(), "Artifact should have been deleted" );

    }

    @Test
    public void readTest()
        throws IOException, URISyntaxException, HttpException, Exception
    {
        this.overwriteUserRole( "test-user", "read-test-role", "1" );

        Gav gav =
            new Gav( this.getTestId(), "artifact", "1.0.0", null, "xml", 0, new Date().getTime(), "", false, false,
                     null, false, null );
        
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        String serviceURI =
            "content/repositories/" + this.getTestRepositoryId() + "/" + this.getRelitiveArtifactPath( gav );

        Response response = RequestFacade.sendMessage( serviceURI, Method.GET );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Artifact should not have been read" );

        TestContainer.getInstance().getTestContext().useAdminForRequests();
        this.giveUserPrivilege( "test-user", "T1" );
        
        TestContainer.getInstance().getTestContext().setUsername( "test-user" );
        TestContainer.getInstance().getTestContext().setPassword( "admin123" );

        response = RequestFacade.sendMessage( serviceURI, Method.GET );
        Assert.assertEquals( 200, response.getStatus().getCode(), "Artifact should have been read\nresponse:\n"+ response.getEntity().getText());

        response = RequestFacade.sendMessage( serviceURI, Method.DELETE );
        Assert.assertEquals( 401, response.getStatus().getCode(), "Artifact should have been deleted" );
        
    }

}
