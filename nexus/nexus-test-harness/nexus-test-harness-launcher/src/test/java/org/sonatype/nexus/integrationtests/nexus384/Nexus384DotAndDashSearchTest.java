package org.sonatype.nexus.integrationtests.nexus384;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.NexusArtifact;
import org.sonatype.nexus.test.utils.SearchMessageUtil;

/**
 * Searches for artifact that has a '.' and a '-' in the artifact name.
 */
public class Nexus384DotAndDashSearchTest
    extends AbstractNexusIntegrationTest
{
    @BeforeClass
    public static void cleanWorkFolder()
        throws Exception
    {
        cleanWorkDir();
    }

    protected SearchMessageUtil messageUtil;

    public Nexus384DotAndDashSearchTest()
    {
        this.messageUtil = new SearchMessageUtil();
    }

    
    private void printResults( String title, List<NexusArtifact> results)
    {
        System.out.println( "\n"+title );
        for ( NexusArtifact nexusArtifact : results )
        {
            System.out.println( "artifact: " + nexusArtifact.getGroupId() + ":" + nexusArtifact.getArtifactId() + ":"
                + nexusArtifact.getVersion() + ":" + nexusArtifact.getPackaging() );
        }
    }
    
    @Test
    public void searchAll()
        throws Exception
    {
        // groupId
        List<NexusArtifact> results = messageUtil.searchFor( "nexus384" );
        Assert.assertEquals( 9, results.size() );

        this.printResults( "searchAll", results );

    }

    // look on artifactId and groupId
    @Test
    public void searchDash()
        throws Exception
    { // with dash

        List<NexusArtifact> results = messageUtil.searchFor( "*dash*" );
        Assert.assertEquals( 5, results.size() );
    }

    @Test
    public void searchDot()
        throws Exception
    { // with dot

        List<NexusArtifact> results = messageUtil.searchFor( "dot" );
        this.printResults( "searchDot", results );
        Assert.assertEquals( 5, results.size() );
    }

    @Test
    public void searchDashAndDot()
        throws Exception
    { // with both

        List<NexusArtifact> results = messageUtil.searchFor( "dot dash" );
        Assert.assertEquals( 7, results.size() );
    } // look on groupId

    @Test
    public void searchGroudDashed()
        throws Exception
    { // dashed

        List<NexusArtifact> results = messageUtil.searchFor( "dashed" );
        Assert.assertEquals( 3, results.size() );
    }

    @Test
    public void searchGroudDoted()
        throws Exception
    { // doted

        List<NexusArtifact> results = messageUtil.searchFor( "doted" );
        Assert.assertEquals( 3, results.size() );
    }

    @Test
    public void searchGroudDashedAndDoted()
        throws Exception
    { // both

        List<NexusArtifact> results = messageUtil.searchFor( "dashed doted" );
        Assert.assertEquals( 4, results.size() );
    }

    @Test
    public void searchMixed()
        throws Exception
    { // mixed

        List<NexusArtifact> results = messageUtil.searchFor( "mixed" );
        Assert.assertEquals( 2, results.size() );
    }

    @Test
    public void searchMixedNexus83()
        throws Exception
    { // based on nexus-83
        
        List<NexusArtifact> results = messageUtil.searchFor( "mixed-" );
        Assert.assertEquals( 2, results.size() );
    }
    
    @Test
    public void searchMixed2Nexus83()
        throws Exception
    { // based on nexus-83

        List<NexusArtifact> results = messageUtil.searchFor( "mixed-d" );
        Assert.assertEquals( 2, results.size() );
    }

    @Test
    public void searchForWildArtifactNexus432()
        throws Exception
    { // based on nexus-432

        List<NexusArtifact> results = this.filterResults(messageUtil.searchFor( "*artifact*" ));
        Assert.assertEquals( 5, results.size() );
    }
    
    @Test
    public void searchForArtifactNexus432()
        throws Exception
    { // based on nexus-432

        List<NexusArtifact> results = this.filterResults(messageUtil.searchFor( "artifact" ));
        Assert.assertEquals( 2, results.size() );
    }
    
    private List<NexusArtifact> filterResults( List<NexusArtifact> results)
    {
        List<NexusArtifact> processedResults = new ArrayList<NexusArtifact>();
     // the index might be full of other stuff, process out only things from this test
        for ( NexusArtifact nexusArtifact : results )
        {            
            if( nexusArtifact.getGroupId().contains( "nexus384" ))
            {
                processedResults.add( nexusArtifact );
            }
        }
        return processedResults;
    }

}
