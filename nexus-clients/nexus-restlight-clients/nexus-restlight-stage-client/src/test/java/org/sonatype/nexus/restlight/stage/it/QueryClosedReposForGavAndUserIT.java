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

public class QueryClosedReposForGavAndUserIT
    extends AbstractStageClientIT
{
    @Test
    public void queryClosedRepositoryForGAVAndUser()
        throws IOException, RESTLightClientException, IllegalArtifactCoordinateException
    {
        String profileId = createStagingProfile( "gav-closed-repos-for-user" );

        File jarFile = getArtifactFile( "dummy-1.0.jar" );

        Coordinate coord = new Coordinate( "dummy.test.group", "dummy", "1.0", "jar" );
        deployTo( profileId, jarFile, coord );

        StageClient client = new StageClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        client.finishRepositoryForUser( coord.getGroupId(), coord.getArtifactId(), coord.getVersion(), "Description" );

        List<StageRepository> repos =
            client.getClosedStageRepositoriesForUser( coord.getGroupId(), coord.getArtifactId(), coord.getVersion() );

        assertNotNull( repos );
        assertEquals( 1, repos.size() );
    }
}
