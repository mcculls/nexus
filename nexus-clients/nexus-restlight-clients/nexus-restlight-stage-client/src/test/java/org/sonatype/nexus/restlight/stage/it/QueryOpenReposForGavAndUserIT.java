package org.sonatype.nexus.restlight.stage.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.restlight.common.RESTLightClientException;
import org.sonatype.nexus.restlight.stage.StageClient;
import org.sonatype.nexus.restlight.stage.StageRepository;
import org.sonatype.nexus.restlight.stage.it.utils.Coordinate;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class QueryOpenReposForGavAndUserIT
    extends AbstractStageClientIT
{

    @Test
    public void queryAllOpenRepositoriesForGavAndUser()
        throws IllegalArtifactCoordinateException, IOException, RESTLightClientException
    {
        String profileId = createStagingProfile( "gav-open-repos-for-user", "testTarget" );

        File jarFile = getArtifactFile( "dummy-1.0.jar" );

        Coordinate coord = new Coordinate( "dummy.test.group", "dummy", "1.0", "jar" );
        deployTo( profileId, jarFile, coord );

        StageClient client = new StageClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        // List<StageRepository> repositories =
        // client.getOpenStageRepositoriesForUser( "some.other.group", "another-artifact", coord.getVersion() );
        //
        // System.out.println( repositories );
        //
        // assertTrue( repositories == null || repositories.isEmpty() );
        //
        // assertEquals( 1, repositories.size() );

        List<StageRepository> repositories =
            client.getOpenStageRepositoriesForUser( coord.getGroupId(), coord.getArtifactId(), coord.getVersion() );

        System.out.println( repositories );

        assertNotNull( repositories );

        assertEquals( 1, repositories.size() );
    }

}
