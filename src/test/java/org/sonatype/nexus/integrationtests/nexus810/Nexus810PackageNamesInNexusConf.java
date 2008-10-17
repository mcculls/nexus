package org.sonatype.nexus.integrationtests.nexus810;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.testng.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.sonatype.nexus.configuration.model.CScheduledTask;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.ScheduledServiceAdvancedResource;
import org.sonatype.nexus.rest.model.ScheduledServicePropertyResource;
import org.sonatype.nexus.tasks.descriptors.ReindexTaskDescriptor;
import org.sonatype.nexus.test.utils.NexusConfigUtil;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.testng.annotations.Test;

public class Nexus810PackageNamesInNexusConf
    extends AbstractNexusIntegrationTest
{

    @Test
    public void checkNexusConfForPackageNames()
        throws Exception
    {

        // create a task
        ScheduledServiceAdvancedResource scheduledTask = new ScheduledServiceAdvancedResource();
        scheduledTask.setEnabled( true );
        scheduledTask.setId( null );
        scheduledTask.setName( "taskAdvanced" );
        scheduledTask.setSchedule( "advanced" );
        // A future date
        Date startDate = DateUtils.addDays( new Date(), 10 );
        startDate = DateUtils.round( startDate, Calendar.DAY_OF_MONTH );
        scheduledTask.setCronCommand( "0 0 12 ? * WED" );

        scheduledTask.setTypeId( ReindexTaskDescriptor.ID );

        ScheduledServicePropertyResource prop = new ScheduledServicePropertyResource();
        prop.setId( "repositoryOrGroupId" );
        prop.setValue( "all_repo" );
        scheduledTask.addProperty( prop );

        Assert.assertTrue( TaskScheduleUtil.create( scheduledTask ).isSuccess(), "Expected task to be created: " );
        
        // now check the conf
        List<CScheduledTask> tasks = NexusConfigUtil.getNexusConfig().getTasks();
        Assert.assertTrue( tasks.size() > 0, "Expected at least 1 task in nexus.xml" );
        
        for ( CScheduledTask task : tasks )
        {
            Assert.assertFalse( task.getType().contains( "org.sonatype." ), "Found package name in nexus.xml for task type: "+ task.getType());
        }

    }
}
