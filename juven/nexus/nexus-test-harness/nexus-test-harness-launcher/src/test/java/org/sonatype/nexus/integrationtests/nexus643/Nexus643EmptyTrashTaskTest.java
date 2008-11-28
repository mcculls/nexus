package org.sonatype.nexus.integrationtests.nexus643;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.rest.model.ScheduledServicePropertyResource;
import org.sonatype.nexus.tasks.descriptors.EmptyTrashTaskDescriptor;
import org.sonatype.nexus.tasks.descriptors.properties.EmptyOlderThanDaysPropertyDescriptor;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;

/**
 * Tests empty trash task.
 */
public class Nexus643EmptyTrashTaskTest
    extends AbstractNexusIntegrationTest
{
    @Test
    public void emptyTrashTask()
        throws Exception
    {

        delete( "nexus643" );

        File trashContent = new File( nexusBaseDir, "runtime/work/trash/nexus-test-harness-repo/nexus643" );
        Assert.assertTrue( "Something should be at trash!", trashContent.exists() );

        // Empty trash content older than 1 days
        File oldTrashFile = new File(
            nexusBaseDir,
            "runtime/work/trash/nexus-test-harness-repo/nexus643/artifact-1-1.0.0.pom" );
        File newTrashFile = new File(
            nexusBaseDir,
            "runtime/work/trash/nexus-test-harness-repo/nexus643/artifact-1-1.0.0.jar" );
        oldTrashFile.setLastModified( System.currentTimeMillis() - 24L * 60L * 60L * 1000L * 2 );

        ScheduledServicePropertyResource prop = new ScheduledServicePropertyResource();
        prop.setId( EmptyOlderThanDaysPropertyDescriptor.ID );
        prop.setValue( "1" );

        TaskScheduleUtil.runTask( "Empty Trash Older Than", EmptyTrashTaskDescriptor.ID, prop );

        Assert.assertTrue( "New trash content should be kept! ", newTrashFile.exists() );
        Assert.assertFalse( "Old trash content should be removed!", oldTrashFile.exists() );

        // Empty the whole trash
        TaskScheduleUtil.runTask( "Empty Whole Trash", EmptyTrashTaskDescriptor.ID );

        Assert.assertFalse( "Trash should be empty!", trashContent.exists() );
    }

    private void delete( String groupId )
        throws IOException
    {
        String serviceURI = "service/local/repositories/nexus-test-harness-repo/content/" + groupId + "/";
        Response response = RequestFacade.sendMessage( serviceURI, Method.DELETE );
        Assert.assertTrue( "Unable to delete nexus643 artifacts", response.getStatus().isSuccess() );
    }

}
