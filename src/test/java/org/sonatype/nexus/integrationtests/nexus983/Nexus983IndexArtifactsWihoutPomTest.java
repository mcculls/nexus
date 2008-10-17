package org.sonatype.nexus.integrationtests.nexus983;

import java.io.File;
import java.util.List;

import org.testng.Assert;

import org.apache.commons.io.FileUtils;
import org.aspectj.lang.annotation.Before;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.NexusArtifact;
import org.sonatype.nexus.test.utils.DeployUtils;
import org.sonatype.nexus.test.utils.RepositoryMessageUtil;
import org.sonatype.nexus.test.utils.SearchMessageUtil;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Copy (filesystem copy) a jar to a nexus repo and run reindex to see what happens
 */
public class Nexus983IndexArtifactsWihoutPomTest
    extends AbstractNexusIntegrationTest
{

    protected SearchMessageUtil messageUtil;

    @BeforeTest
    public void ini()
    {
        this.messageUtil = new SearchMessageUtil();
    }

    @Test
    public void deployPomlessArtifact()
        throws Exception
    {
        File artifactFile = getTestFile( "artifact.jar" );
        DeployUtils.deployWithWagon( this.container, "http", baseNexusUrl + "content/repositories/"
            + REPO_TEST_HARNESS_REPO, artifactFile, "nexus983/nexus983-artifact1/1.0.0/nexus983-artifact1-1.0.0.jar" );
        List<NexusArtifact> artifacts = messageUtil.searchFor( "nexus983-artifact1" );
        Assert.assertEquals( 1, artifacts.size(), "Should find one artifact" );
    }

    @Test
    public void copyPomlessArtifact()
        throws Exception
    {
        File artifactFile = getTestFile( "artifact.jar" );
        FileUtils.copyFile( artifactFile, new File( nexusBaseDir, "runtime/work/storage/" + REPO_TEST_HARNESS_REPO
            + "/nexus983/nexus983-artifact2/1.0.0/nexus983-artifact2-1.0.0.jar" ) );
        RepositoryMessageUtil.updateIndexes( REPO_TEST_HARNESS_REPO );

        List<NexusArtifact> artifacts = messageUtil.searchFor( "nexus983-artifact2" );
        Assert.assertEquals( 1, artifacts.size(), "Should find one artifact" );
    }

}
