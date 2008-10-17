package org.sonatype.nexus.integrationtests.nexus640;

import java.io.File;

import org.testng.Assert;

import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.ScheduledServicePropertyResource;
import org.sonatype.nexus.tasks.descriptors.RebuildAttributesTaskDescriptor;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.testng.annotations.Test;

/**
 * Tests the rebuild repository attributes task.
 */
public class Nexus640RebuildRepositoryAttributesTaskTest
    extends AbstractNexusIntegrationTest
{

    @Test
    public void rebuildAttributes()
        throws Exception
    {
        String attributePath = "runtime/work/proxy/attributes/nexus-test-harness-repo/nexus640/artifact/1.0.0/";

        ScheduledServicePropertyResource repo = new ScheduledServicePropertyResource();
        repo.setId( "repositoryOrGroupId" );
        repo.setValue( "repo_" + REPO_TEST_HARNESS_REPO );
        TaskScheduleUtil.runTask( RebuildAttributesTaskDescriptor.ID, repo );

        File jar = new File( nexusBaseDir, attributePath + "artifact-1.0.0.jar" );
        Assert.assertTrue( jar.exists(), "Attribute files should be generated after rebuild" );
        File pom = new File( nexusBaseDir, attributePath + "artifact-1.0.0.pom" );
        Assert.assertTrue( pom.exists(), "Attribute files should be generated after rebuild" );

    }

}
