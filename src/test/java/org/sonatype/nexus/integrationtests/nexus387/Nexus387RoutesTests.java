package org.sonatype.nexus.integrationtests.nexus387;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.testng.Assert;

import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.testng.annotations.Test;

/**
 * Blocking, Exclusive, Inclusive Routes Tests
 */
public class Nexus387RoutesTests
    extends AbstractNexusIntegrationTest
{

    @Test
    public void testExclusive()
        throws IOException
    {

        Gav gav = new Gav(
            this.getTestId() + ".exclusive",
            "exclusive",
            "1.0.0",
            null,
            "jar",
            0,
            new Date().getTime(),
            "Simple Test Artifact",
            false,
            false,
            null,
            false,
            null );

        try
        {
            // should fail
            this.downloadArtifactFromGroup( "exclusive-single", gav, "target/downloads/exclude" );
            Assert.fail( "Resource should not have been found." );
        }
        catch ( IOException e )
        {
        }

        File artifact = this.downloadArtifactFromGroup( "exclusive-group", gav, "target/downloads/exclude" );
        Assert.assertNotNull( artifact );

        String line = this.getFirstLineOfFile( artifact );
        Assert.assertEquals( "exclusive2", line, "Jar contained: " + this.getFirstLineOfFile( artifact )
            + ", expected: exclusive2" );

        artifact = this.downloadArtifactFromGroup( "other-group", gav, "target/downloads/exclude" );
        Assert.assertNotNull( artifact );

        line = this.getFirstLineOfFile( artifact );
        Assert.assertEquals( "exclusive1", line, "Jar contained: " + line + ", expected: exclusive1" );

    }

    @Test
    public void testInclusive()
        throws IOException
    {

        Gav gav = new Gav(
            this.getTestId() + ".inclusive",
            "inclusive",
            "1.0.0",
            null,
            "jar",
            0,
            new Date().getTime(),
            "Simple Test Artifact",
            false,
            false,
            null,
            false,
            null );

        File artifact = this.downloadArtifactFromGroup( "inclusive-single", gav, "target/downloads/include" );

        String line = this.getFirstLineOfFile( artifact );
        Assert.assertEquals( "inclusive1", line, "Jar contained: " + this.getFirstLineOfFile( artifact )
            + ", expected: inclusive1" );

        artifact = this.downloadArtifactFromGroup( "inclusive-group", gav, "target/downloads/include" );

        line = this.getFirstLineOfFile( artifact );
        Assert.assertEquals( "inclusive2", line, "Jar contained: " + this.getFirstLineOfFile( artifact )
            + ", expected: inclusive2" );

        artifact = this.downloadArtifactFromGroup( "other-group", gav, "target/downloads/include" );

        line = this.getFirstLineOfFile( artifact );
        Assert.assertEquals( "inclusive1", line, "Jar contained: " + this.getFirstLineOfFile( artifact )
            + ", expected: inclusive1" );

    }

    @Test
    public void testBlocking()
        throws IOException
    {

        Gav gav = new Gav(
            this.getTestId() + ".blocking",
            "blocking",
            "1.0.0",
            null,
            "jar",
            0,
            new Date().getTime(),
            "Simple Test Artifact",
            false,
            false,
            null,
            false,
            null );

        try
        {

            this.downloadArtifactFromGroup( "blocking-group", gav, "target/downloads/blocking" );
            Assert.fail( "This file should not have been found." );

        }
        catch ( IOException e )
        {
        }
        File artifact = this.downloadArtifactFromGroup( "other-group", gav, "target/downloads/blocking" );

        String line = this.getFirstLineOfFile( artifact );
        Assert.assertEquals( "blocking1", line, "Jar contained: " + this.getFirstLineOfFile( artifact )
            + ", expected: blocking1" );

    }

    private String getFirstLineOfFile( File file )
        throws IOException
    {
        BufferedReader bReader = new BufferedReader( new FileReader( file ) );
        String line = bReader.readLine().trim(); // only need one line
        bReader.close();

        return line;

    }

}
