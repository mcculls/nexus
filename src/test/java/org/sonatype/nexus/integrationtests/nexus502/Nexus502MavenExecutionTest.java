package org.sonatype.nexus.integrationtests.nexus502;

import java.io.File;
import java.io.IOException;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.aspectj.lang.annotation.Before;
import org.restlet.data.MediaType;
import org.sonatype.nexus.integrationtests.AbstractMavenNexusIT;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.rest.model.UserResource;
import org.sonatype.nexus.test.utils.UserMessageUtil;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Put a bunch of artifacts in a repo, and then run a maven project to download them 
 */
public class Nexus502MavenExecutionTest
    extends AbstractMavenNexusIT
{

    static
    {
        TestContainer.getInstance().getTestContext().setSecureTest( true );
    }

    private Verifier verifier;

    @BeforeTest
    public void createVerifier()
        throws Exception
    {
        File mavenProject = getTestFile( "maven-project" );
        File settings = getTestFile( "repositories.xml" );
        verifier = createVerifier( mavenProject, settings );
    }

    @Test
    public void dependencyDownload()
        throws Exception
    {
        try
        {
            verifier.executeGoal( "dependency:resolve" );
            verifier.verifyErrorFreeLog();
        }
        catch ( VerificationException e )
        {
            failTest( verifier );
        }
    }

    @Test
    public void dependencyDownloadPrivateServer()
        throws Exception
    {
        // Disable anonymous
        disableUser( "anonymous" );

        try
        {
            verifier.executeGoal( "dependency:resolve" );
            verifier.verifyErrorFreeLog();
            failTest( verifier );
        }
        catch ( VerificationException e )
        {
            // Expected exception
        }
    }

    private UserResource disableUser( String userId )
        throws IOException
    {
        UserMessageUtil util =
            new UserMessageUtil( this.getXMLXStream(), MediaType.APPLICATION_XML );
        return util.disableUser( userId );
    }

    // Depends on nexus-508
    @Test
    public void dependencyDownloadProtectedServer()
        throws Exception
    {
        // Disable anonymous
        disableUser( "anonymous" );

        File mavenProject = getTestFile( "maven-project" );
        File settings = getTestFile( "repositoriesWithAuthentication.xml" );

        Verifier verifier = createVerifier( mavenProject, settings );
        verifier.executeGoal( "dependency:resolve" );
        verifier.verifyErrorFreeLog();
    }

}
