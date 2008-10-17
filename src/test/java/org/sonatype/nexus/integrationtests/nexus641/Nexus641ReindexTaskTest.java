package org.sonatype.nexus.integrationtests.nexus641;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.testng.Assert;

import org.apache.commons.io.FileUtils;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.NexusArtifact;
import org.sonatype.nexus.rest.model.ScheduledServiceListResource;
import org.sonatype.nexus.rest.model.ScheduledServicePropertyResource;
import org.sonatype.nexus.tasks.descriptors.ReindexTaskDescriptor;
import org.sonatype.nexus.test.utils.SearchMessageUtil;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.testng.annotations.Test;

/**
 * Test task Reindex Repositories.
 * @author marvin
 */
public class Nexus641ReindexTaskTest
    extends AbstractNexusIntegrationTest
{
    
    private SearchMessageUtil messageUtil = new SearchMessageUtil();
    
    private File repositoryPath = new File( nexusBaseDir, "runtime/work/storage/"+ this.getTestRepositoryId() );

    
    public Nexus641ReindexTaskTest() throws IOException
    {
        super( "nexus641" );
        
    }
    
    @Test
    public void testReindex()
        throws Exception
    {

        this.repositoryPath = new File( nexusBaseDir, "runtime/work/storage/"+ this.getTestRepositoryId() );
        System.out.println( "path: "+ repositoryPath );
        File oldSnapshot = getTestFile( "repo" );

        // Copy artifact to avoid indexing
        FileUtils.copyDirectory( oldSnapshot, repositoryPath );

        // try to seach and fail
        List<NexusArtifact> search = messageUtil.searchFor( "nexus641" );
        Assert.assertEquals(1, search.size(), "The artifact was already indexed" );

        // reindex
        ScheduledServicePropertyResource prop = new ScheduledServicePropertyResource();
        prop.setId( "repositoryOrGroupId" );
        prop.setValue( "nexus-test-harness-repo" );

        // reindex
        ScheduledServiceListResource task = TaskScheduleUtil.runTask( ReindexTaskDescriptor.ID, prop );
        Assert.assertNotNull( task );
        Assert.assertEquals( "SUBMITTED", task.getStatus() );
        
        // try to download again and success
        search = messageUtil.searchFor( "nexus641" );
        Assert.assertEquals( 2, search.size(),"The artifact should be indexed" );
    }

}
