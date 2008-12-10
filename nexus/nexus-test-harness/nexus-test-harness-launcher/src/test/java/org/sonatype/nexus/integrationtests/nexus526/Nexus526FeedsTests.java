package org.sonatype.nexus.integrationtests.nexus526;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.FeedUtil;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Tests for deployment entries in feeds.
 */
public class Nexus526FeedsTests
    extends AbstractNexusIntegrationTest
{

    private Gav gav;

    public Nexus526FeedsTests()
    {
        super( "nexus-test-harness-repo" );
        this.gav = new Gav(
            this.getTestId(),
            "artifact1",
            "1.0.0",
            null,
            "jar",
            0,
            new Date().getTime(),
            "Artifact 1",
            false,
            false,
            null,
            false,
            null );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void recentCachedOrDeployedFileFeedTest()
        throws Exception
    {
        SyndFeed feed = FeedUtil.getFeed( "recentlyCachedOrDeployedFiles" );

        List<SyndEntry> entries = feed.getEntries();

        Assert.assertTrue( entries.size() >= 2 );

        List<SyndEntry> latestEntries = new ArrayList<SyndEntry>( 2 );

        latestEntries.add( entries.get( 0 ) );

        latestEntries.add( entries.get( 1 ) );

        validateFileInFeedEntries( latestEntries );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void recentChangedFileFeedTest()
        throws Exception
    {
        SyndFeed feed = FeedUtil.getFeed( "recentlyChangedFiles" );

        List<SyndEntry> entries = feed.getEntries();

        Assert.assertTrue( entries.size() >= 2 );

        List<SyndEntry> latestEntries = new ArrayList<SyndEntry>( 2 );

        latestEntries.add( entries.get( 0 ) );

        latestEntries.add( entries.get( 1 ) );

        validateFileInFeedEntries( latestEntries );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void recentDeployedFileFeedTest()
        throws Exception
    {
        SyndFeed feed = FeedUtil.getFeed( "recentlyDeployedFiles" );

        List<SyndEntry> entries = feed.getEntries();

        Assert.assertTrue( entries.size() >= 2 );

        List<SyndEntry> latestEntries = new ArrayList<SyndEntry>( 2 );

        latestEntries.add( entries.get( 0 ) );

        latestEntries.add( entries.get( 1 ) );

        validateFileInFeedEntries( latestEntries );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void recentCachedOrDeployedArtifactFeedTest()
        throws Exception
    {
        SyndFeed feed = FeedUtil.getFeed( "recentlyCachedOrDeployedArtifacts" );

        List<SyndEntry> entries = feed.getEntries();

        // although there are 2 files, but that is only 1 Maven artifact
        Assert.assertTrue( entries.size() >= 1 );

        List<SyndEntry> latestEntries = new ArrayList<SyndEntry>( 1 );

        latestEntries.add( entries.get( 0 ) );

        validateArtifactInFeedEntries( latestEntries );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void recentChangedFileArtifactTest()
        throws Exception
    {
        SyndFeed feed = FeedUtil.getFeed( "recentlyChangedArtifacts" );

        List<SyndEntry> entries = feed.getEntries();

        Assert.assertTrue( entries.size() >= 1 );

        List<SyndEntry> latestEntries = new ArrayList<SyndEntry>( 1 );

        latestEntries.add( entries.get( 0 ) );

        validateArtifactInFeedEntries( latestEntries );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void recentDeployedArtifactFeedTest()
        throws Exception
    {
        SyndFeed feed = FeedUtil.getFeed( "recentlyDeployedArtifacts" );

        List<SyndEntry> entries = feed.getEntries();

        Assert.assertTrue( entries.size() >= 1 );

        List<SyndEntry> latestEntries = new ArrayList<SyndEntry>( 1 );

        latestEntries.add( entries.get( 0 ) );

        validateArtifactInFeedEntries( latestEntries );
    }

    private void validateArtifactInFeedEntries( List<SyndEntry> entries )
        throws Exception
    {
        String link = getBaseNexusUrl() + "content/repositories/" + getTestRepositoryId() + "/"
            + getRelitiveArtifactPath( gav.getGroupId(), gav.getArtifactId(), gav.getVersion(), "pom", null );

        for ( SyndEntry entry : entries )
        {
            // check if the title contains the groupid, artifactid, and version
            String title = entry.getTitle();

            Assert.assertTrue( "Feed title does not contain the groupId. Title was: " + title, title.contains( gav
                .getGroupId() ) );

            Assert.assertTrue( "Feed title does not contain the artifactId. Title was: " + title, title.contains( gav
                .getArtifactId() ) );

            Assert.assertTrue( "Feed title does not contain the version. Title was: " + title, title.contains( gav
                .getVersion() ) );

            Assert.assertEquals( link, entry.getLink() );
        }
    }

    private void validateFileInFeedEntries( List<SyndEntry> entries )
        throws Exception
    {
        String pomName = gav.getArtifactId() + "-" + gav.getVersion() + ".pom";

        String contentName = gav.getArtifactId() + "-" + gav.getVersion() + "." + gav.getExtension();

        for ( SyndEntry entry : entries )
        {
            // check if the title contains the file name (pom or jar)
            String title = entry.getTitle();

            Assert.assertTrue( title.contains( pomName ) || title.contains( contentName ) );
        }
    }
}
