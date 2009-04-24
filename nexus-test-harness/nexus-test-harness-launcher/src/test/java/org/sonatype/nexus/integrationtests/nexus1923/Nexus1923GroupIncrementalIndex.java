package org.sonatype.nexus.integrationtests.nexus1923;

import junit.framework.Assert;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

public class Nexus1923GroupIncrementalIndex
    extends AbstractNexus1923
{
    public Nexus1923GroupIncrementalIndex()
        throws Exception
    {
        super();
    }

    @Test
    public void validateIncrementalIndexesCreated()
        throws Exception
    {
        createHostedRepository();
        createSecondHostedRepository();
        createThirdHostedRepository();

        createGroup( GROUP_ID, HOSTED_REPO_ID, SECOND_HOSTED_REPO_ID, THIRD_HOSTED_REPO_ID );

        String reindexId = createReindexTask( GROUP_ID, GROUP_REINDEX_TASK_NAME );

        FileUtils.copyDirectoryStructure( getTestFile( FIRST_ARTIFACT ), getHostedRepositoryStorageDirectory() );

        reindexRepository( reindexId, GROUP_REINDEX_TASK_NAME );

        Assert.assertTrue( getHostedRepositoryIndex().exists() );
        Assert.assertTrue( getHostedRepositoryIndexIncrement( "1" ).exists() );
        Assert.assertFalse( getHostedRepositoryIndexIncrement( "2" ).exists() );
        validateCurrentHostedIncrementalCounter( 1 );
        
        Assert.assertTrue( getSecondHostedRepositoryIndex().exists() );
        Assert.assertFalse( getSecondHostedRepositoryIndexIncrement( "1" ).exists() );
        validateCurrentSecondHostedIncrementalCounter( 0 );
        
        Assert.assertTrue( getThirdHostedRepositoryIndex().exists() );
        Assert.assertFalse( getThirdHostedRepositoryIndexIncrement( "1" ).exists() );
        validateCurrentThirdHostedIncrementalCounter( 0 );

        // Group doesnt create index on creation, so reindex is first pass at creating, thus no
        // incremental piece
        Assert.assertTrue( getGroupIndex().exists() );
        Assert.assertFalse( getGroupIndexIncrement( "1" ).exists() );
        validateCurrentGroupIncrementalCounter( 0 );

        FileUtils.copyDirectoryStructure( getTestFile( SECOND_ARTIFACT ), getSecondHostedRepositoryStorageDirectory() );

        reindexRepository( reindexId, GROUP_REINDEX_TASK_NAME );

        Assert.assertTrue( getHostedRepositoryIndex().exists() );
        Assert.assertFalse( getHostedRepositoryIndexIncrement( "1" ).exists() );
        validateCurrentHostedIncrementalCounter( null );

        Assert.assertTrue( getSecondHostedRepositoryIndex().exists() );
        Assert.assertTrue( getSecondHostedRepositoryIndexIncrement( "1" ).exists() );
        Assert.assertFalse( getSecondHostedRepositoryIndexIncrement( "2" ).exists() );
        validateCurrentHostedIncrementalCounter( null );

        Assert.assertTrue( getGroupIndex().exists() );
        Assert.assertTrue( getGroupIndexIncrement( "1" ).exists() );
        Assert.assertTrue( getGroupIndexIncrement( "2" ).exists() );
        //validateCurrentGroupIncrementalCounter( null );

    }
}
