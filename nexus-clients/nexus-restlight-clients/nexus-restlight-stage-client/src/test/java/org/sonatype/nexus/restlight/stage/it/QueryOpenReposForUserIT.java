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

public class QueryOpenReposForUserIT
    extends AbstractStageClientIT
{

    @Test
    public void queryAllOpenRepositoriesForUser()
        throws IllegalArtifactCoordinateException, IOException, RESTLightClientException
    {
        String profileId = createStagingProfile( "all-open-repos-for-user" );

        File jarFile = getArtifactFile( "dummy-1.0.jar" );

        deployTo( profileId, jarFile, new Coordinate( "dummy", "dummy", "1.0", "jar" ) );

        StageClient client = new StageClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        List<StageRepository> repositories = client.getOpenStageRepositoriesForUser();

        assertNotNull( repositories );

        System.out.println( repositories );

        assertEquals( 1, repositories.size() );
    }
}
