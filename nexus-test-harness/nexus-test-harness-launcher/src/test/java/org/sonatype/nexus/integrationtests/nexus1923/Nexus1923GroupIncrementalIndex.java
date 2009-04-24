package org.sonatype.nexus.integrationtests.nexus1923;

import java.io.File;
import java.util.Properties;

import junit.framework.Assert;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

public class Nexus1923GroupIncrementalIndex
    extends AbstractNexus1923
{
    private static final String INDEX_GROUP = "index_group";

    private static final String INDEX_GROUP_TASK = INDEX_GROUP + "-task";

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
        createOtherHostedRepository();
        createThirdHostedRepository();

        createGroup( INDEX_GROUP, HOSTED_REPO_ID, OTHER_HOSTED_REPO_ID, THIRD_HOSTED_REPO_ID );

        String reindexId = createReindexTask( INDEX_GROUP, INDEX_GROUP_TASK );

        FileUtils.copyDirectoryStructure( getTestFile( FIRST_ARTIFACT ), getHostedRepositoryStorageDirectory() );

        reindexRepository( reindexId, INDEX_GROUP_TASK );

        Assert.assertTrue( getHostedRepositoryIndex().exists() );
        Assert.assertFalse( getHostedRepositoryIndexIncrement( "1" ).exists() );
        validateCurrentHostedIncrementalCounter( null );

        Assert.assertTrue( getGroupIndex().exists() );
        Assert.assertFalse( getGroupIndexIncrement( "1" ).exists() );
        validateCurrentGroupIncrementalCounter( null );

        FileUtils.copyDirectoryStructure( getTestFile( SECOND_ARTIFACT ), getOtherHostedRepositoryStorageDirectory() );

        reindexRepository( reindexId, INDEX_GROUP_TASK );

        Assert.assertTrue( getOtherHostedRepositoryIndex().exists() );
        Assert.assertFalse( getOtherHostedRepositoryIndexIncrement( "1" ).exists() );
        validateCurrentHostedIncrementalCounter( null );

        Assert.assertTrue( getGroupIndex().exists() );
        Assert.assertFalse( getGroupIndexIncrement( "1" ).exists() );
        Assert.assertFalse( getGroupIndexIncrement( "2" ).exists() );
        validateCurrentGroupIncrementalCounter( null );

    }

    protected void validateCurrentGroupIncrementalCounter( Integer current )
        throws Exception
    {
        validateCurrentIncrementalCounter( getGroupIndexProperties(), current );
    }

    private Properties getGroupIndexProperties()
        throws Exception
    {
        return getRepositoryIndexProperties( getGroupStorageIndexDirectory() );
    }

    private File getGroupIndexIncrement( String id )
    {
        return getRepositoryIndexIncrement( getGroupStorageIndexDirectory(), id );
    }

    private File getGroupIndex()
    {
        return getRepositoryIndex( getGroupStorageIndexDirectory() );
    }

    protected File getGroupStorageIndexDirectory()
    {
        return getRepositoryStorageIndexDirectory( INDEX_GROUP );
    }
}
