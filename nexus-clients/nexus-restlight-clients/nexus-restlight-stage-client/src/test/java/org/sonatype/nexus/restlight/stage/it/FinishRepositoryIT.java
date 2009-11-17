package org.sonatype.nexus.restlight.stage.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.restlight.common.RESTLightClientException;
import org.sonatype.nexus.restlight.stage.StageClient;
import org.sonatype.nexus.restlight.stage.StageRepository;
import org.sonatype.nexus.restlight.stage.it.utils.Coordinate;

import java.io.File;
import java.io.IOException;

public class FinishRepositoryIT
    extends AbstractStageClientIT
{

    @Test
    public void finishRepository()
        throws IOException, RESTLightClientException, IllegalArtifactCoordinateException
    {
        String profileId = createStagingProfile( "finish-repo" );

        File jarFile = getArtifactFile( "dummy-1.0.jar" );

        Coordinate coord = new Coordinate( "dummy.test.group", "dummy", "1.0", "jar" );
        deployTo( profileId, jarFile, coord );

        StageClient client = new StageClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        StageRepository repo =
            client.getOpenStageRepositoryForUser( coord.getGroupId(), coord.getArtifactId(), coord.getVersion() );

        assertNotNull( repo );

        client.finishRepository( repo, "this is a description" );

        repo = client.getOpenStageRepositoryForUser( coord.getGroupId(), coord.getArtifactId(), coord.getVersion() );

        assertNull( repo );
    }

}
