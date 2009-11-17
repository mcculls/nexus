package org.sonatype.nexus.restlight.stage.it;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.junit.After;
import org.junit.BeforeClass;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.restlight.stage.it.utils.Coordinate;
import org.sonatype.nexus.restlight.stage.it.utils.StagingMessageUtil;
import org.sonatype.nexus.restlight.testharness.AbstractRestlightIT;
import org.sonatype.nexus.test.utils.DeployUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.AssertionFailedError;

import com.sonatype.nexus.staging.api.dto.StagingProfileDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileRepositoryDTO;

public abstract class AbstractStageClientIT
    extends AbstractRestlightIT
{
    protected StagingMessageUtil stagingMessageUtil = new StagingMessageUtil();

    private final Set<String> profilesToDelete = new HashSet<String>();

    // @Override
    // protected void copyConfigFiles()
    // throws IOException
    // {
    // super.copyConfigFiles();
    // this.copyConfigFile( "staging.xml", WORK_CONF_DIR );
    // }

    @BeforeClass
    public static void setSecure()
    {
        TestContainer.getInstance().getTestContext().setSecureTest( true );
    }

    @After
    public void deleteCreatedProfiles()
    {
        for ( String profileId : profilesToDelete )
        {
            try
            {
                stagingMessageUtil.deleteProfile( profileId );
            }
            catch ( IOException e )
            {
            }
        }
    }

    protected String createStagingProfile( final String name )
        throws IOException
    {
        return createStagingProfile( name, "allRepos", Collections.singleton( "public" ) );
    }

    protected String createStagingProfile( final String name, final String repoTargetId )
        throws IOException
    {
        return createStagingProfile( name, repoTargetId, Collections.singleton( "public" ) );
    }

    protected String createStagingProfile( final String name, final String repoTargetId, final Set<String> targetGroups )
        throws IOException
    {
        StagingProfileDTO dto = new StagingProfileDTO();

        dto.setName( name );
        dto.setRepositoryTemplateId( "default_hosted_release" );
        dto.setRepositoryType( "maven2" );
        dto.setRepositoryTargetId( repoTargetId );
        dto.getTargetGroups().addAll( targetGroups );

        String id = stagingMessageUtil.createProfile( dto ).getId();
        profilesToDelete.add( id );

        return id;
    }

    protected void deployTo( final String profileId, final File artifactFile, final Coordinate coord )
        throws IOException, IllegalArtifactCoordinateException
    {
        String deployUrl = getBaseNexusUrl() + "service/local/staging/deploy/maven2";

        List<StagingProfileRepositoryDTO> repos = stagingMessageUtil.getRepositoryList( profileId );
        int repoSz = repos.size();

        Gav gav = coord.toGav();

        try
        {
            DeployUtils.deployWithWagon( container, "http", deployUrl, artifactFile, getRelitiveArtifactPath( gav ) );
        }
        catch ( ConnectionException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }
        catch ( AuthenticationException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }
        catch ( TransferFailedException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }
        catch ( ResourceDoesNotExistException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }
        catch ( AuthorizationException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }
        catch ( ComponentLookupException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }
        catch ( InterruptedException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }
        catch ( CommandLineException e )
        {
            throw new IllegalStateException( "Failed to deploy: " + coord, e );
        }

        repos = stagingMessageUtil.getRepositoryList( profileId );
        assertEquals( "New staging repository was NOT added.", repoSz + 1, repos.size() );
    }

    // allow override.
    protected String getTestFolderName()
    {
        return null;
    }

    @Override
    protected String getTestId()
    {
        String folderName = getTestFolderName();
        return folderName == null ? super.getTestId() : folderName;
    }

    protected File getArtifactFile( final String relativePath )
    {
        File result;
        String resource = getTestId() + "/" + relativePath;

        ClassLoader cloader = Thread.currentThread().getContextClassLoader();

        URL url = cloader.getResource( resource );
        if ( url == null )
        {
            url = cloader.getResource( "default/" + relativePath );
        }

        if ( url == null )
        {
            fail( "Cannot find test resource: " + relativePath );
        }

        try
        {
            result = new File( url.toURI().normalize() );
        }
        catch ( URISyntaxException e )
        {
            AssertionFailedError error =
                new AssertionFailedError( "Failed to locate test resource on classpath "
                    + "(after failing in test.resources.folder). Reason: " + e.getMessage() );

            error.initCause( e );

            throw error;
        }

        if ( result == null )
        {
            result = super.getTestResourceAsFile( relativePath );
        }

        return result;
    }

    @Override
    protected String getExpectedPassword()
    {
        return TestContainer.getInstance().getTestContext().getPassword();
    }

    @Override
    protected String getExpectedUser()
    {
        return TestContainer.getInstance().getTestContext().getUsername();
    }
}
