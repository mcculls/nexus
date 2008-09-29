package org.sonatype.nexus.index;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.sonatype.nexus.index.context.IndexContextInInconsistentStateException;

public class Nexus384DotsDashesNexusIndexerTest
    extends AbstractNexusIndexerTest
{

    protected File repo = new File( getBasedir(), "src/test/nexus-384" );

    @Override
    protected void prepareNexusIndexer( NexusIndexer nexusIndexer )
        throws Exception
    {
        context = nexusIndexer.addIndexingContext(
            "nexus-384",
            "nexus-384",
            repo,
            indexDir,
            null,
            null,
            NexusIndexer.DEFAULT_INDEX,
            false );
        nexusIndexer.scan( context );

    }

    private String getResutlsAsString( FlatSearchResponse results )
    {
        StringBuffer sb = new StringBuffer("Query: ");
        sb.append( results.getQuery() ).append( "\n" );
        
        for ( ArtifactInfo artifactInfo : results.getResults() )
        {
            sb.append( "artifact: " ).append( artifactInfo ).append( "\n" );
        }
        sb.append( "\n" );
        return sb.toString();
    }

    public void testArtifactSuffixSearch()
        throws IOException,
            IndexContextInInconsistentStateException
    {
        Query q = nexusIndexer.constructQuery( ArtifactInfo.ARTIFACT_ID, "*artifact" );

        FlatSearchRequest req = new FlatSearchRequest( q );

        FlatSearchResponse result = nexusIndexer.searchFlat( req );

        assertEquals( req.getQuery().toString(), 5, result.getTotalHits() );
    }
    
    public void testSearch() throws IOException, IndexContextInInconsistentStateException
    {   
        this.doSearch( "nexus384", 9 );
        this.doSearch( "dash", 5 );
        this.doSearch( "*dash*", 5 );
        this.doSearch( "dot", 5 );
        this.doSearch( "dot dash", 7 );
        this.doSearch( "dashed", 3 );
        this.doSearch( "doted", 3 );
        this.doSearch( "dashed doted", 4 );
        this.doSearch( "mixed", 2 );
        this.doSearch( "mixed-", 2 );
        this.doSearch( "mixed-d", 2 );
        this.doSearch( "*artifact*", 5 );
    }


    private FlatSearchResponse doSearch( String term, int expectedResutls ) throws IOException, IndexContextInInconsistentStateException
    {
        
        Query q1 = nexusIndexer.constructQuery( ArtifactInfo.GROUP_ID, term );

        Query q2 = nexusIndexer.constructQuery( ArtifactInfo.ARTIFACT_ID, term );

        BooleanQuery bq = new BooleanQuery();

        bq.add( q1, BooleanClause.Occur.SHOULD );

        bq.add( q2, BooleanClause.Occur.SHOULD );
        
        FlatSearchRequest req = new FlatSearchRequest( bq, ArtifactInfo.REPOSITORY_VERSION_COMPARATOR );

        FlatSearchResponse result = nexusIndexer.searchFlat( req );
        
        assertEquals( term + " -> " +"\n"+this.getResutlsAsString( result ), expectedResutls, result.getTotalHits() );
        
        return result;
    }

}
