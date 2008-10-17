package org.sonatype.nexus.integrationtests.nexus643;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;

import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.tasks.descriptors.EmptyTrashTaskDescriptor;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.testng.annotations.Test;

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
        Assert.assertTrue( trashContent.exists(),"Something should be at trash!" );

        // This is THE important part
        TaskScheduleUtil.runTask( EmptyTrashTaskDescriptor.ID );

        Assert.assertFalse( trashContent.exists(),"Trash should be empty!" );
    }

    private void delete( String groupId )
        throws IOException
    {
        String serviceURI = "service/local/repositories/nexus-test-harness-repo/content/" + groupId + "/";
        Response response = RequestFacade.sendMessage( serviceURI, Method.DELETE );
        Assert.assertTrue( response.getStatus().isSuccess(),"Unable to delete nexus643 artifacts" );
    }
}
