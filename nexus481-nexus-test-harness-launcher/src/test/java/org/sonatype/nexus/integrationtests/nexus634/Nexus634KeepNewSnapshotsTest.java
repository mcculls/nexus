package org.sonatype.nexus.integrationtests.nexus634;

import java.io.File;
import java.util.Collection;

import org.testng.Assert;

import org.testng.annotations.Test;

/**
 * Test SnapshotRemoverTask to remove old artifacts but keep updated artifacts
 * @author marvin
 */
public class Nexus634KeepNewSnapshotsTest
    extends AbstractSnapshotRemoverTest
{

    @Test
    public void keepNewSnapshots()
        throws Exception
    {

        // This is THE important part
        runSnapshotRemover( "nexus-test-harness-snapshot-repo", 0, 10, true );

        Collection<File> jars = listFiles( artifactFolder, new String[] { "jar" }, false );
        Assert.assertEquals( 1, jars.size(), "SnapshotRemoverTask should remove only old artifacts" );
    }

}
