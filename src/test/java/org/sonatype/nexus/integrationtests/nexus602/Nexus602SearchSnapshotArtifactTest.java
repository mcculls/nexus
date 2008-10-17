package org.sonatype.nexus.integrationtests.nexus602;

import java.net.URL;

import org.testng.Assert;

import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.testng.annotations.Test;

/**
 * Test snapshot search results can be downloaded.
 */
public class Nexus602SearchSnapshotArtifactTest
    extends AbstractNexusIntegrationTest
{

    private static final Gav SNAPSHOT_ARTIFACT =
        new Gav( "nexus602", "artifact", "1.0-SNAPSHOT", null, "jar", 0, 0L, null, false, false, null, false, null );

    @Test
    public void searchSnapshot()
        throws Exception
    {
        String serviceURI =
            "service/local/artifact/maven/redirect?r=" + REPO_TEST_HARNESS_SNAPSHOT_REPO + "&g="
                + SNAPSHOT_ARTIFACT.getGroupId() + "&a=" + SNAPSHOT_ARTIFACT.getArtifactId() + "&v="
                + SNAPSHOT_ARTIFACT.getVersion();
        Response response = RequestFacade.doGetRequest( serviceURI );
        Assert.assertEquals( 301, response.getStatus().getCode(),"Snapshot download should redirect to a new file "
            + response.getRequest().getResourceRef().toString() );

        Reference redirectRef = response.getRedirectRef();
        Assert.assertNotNull( redirectRef, "Snapshot download should redirect to a new file "
            + response.getRequest().getResourceRef().toString() );

        serviceURI = redirectRef.toString();

        response = RequestFacade.sendMessage( new URL( serviceURI ), Method.GET, null );

        Assert.assertTrue( response.getStatus().isSuccess(), "Unable to fetch snapshot artifact" );
    }

    @Test
    public void searchRelease()
        throws Exception
    {
        String serviceURI =
            "service/local/artifact/maven/redirect?r=" + REPO_TEST_HARNESS_REPO + "&g=" + getTestId()
                + "&a=" + "artifact" + "&v=" + "1.0";
        Response response = RequestFacade.doGetRequest( serviceURI );

        Assert.assertEquals( 301,
                             response.getStatus().getCode(), "Should redirect to a new file " + response.getRequest().getResourceRef().toString() );

        Reference redirectRef = response.getRedirectRef();
        Assert.assertNotNull( 
                              redirectRef, "Should redirect to a new file " + response.getRequest().getResourceRef().toString() );

        serviceURI = redirectRef.toString();

        response = RequestFacade.sendMessage( new URL( serviceURI ), Method.GET, null );

        Assert.assertTrue( response.getStatus().isSuccess(), "fetch released artifact" );
    }

    @Test
    public void searchInvalidArtifact()
        throws Exception
    {
        String serviceURI =
            "service/local/artifact/maven/redirect?r=" + REPO_TEST_HARNESS_REPO + "&g=" + "invalidGroupId"
                + "&a=" + "invalidArtifact" + "&v=" + "32.64";
        Response response = RequestFacade.doGetRequest( serviceURI );

        Assert.assertEquals( 404, response.getStatus().getCode(), "Shouldn't find an invalid artifact" );
    }

}
