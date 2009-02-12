/**
 * Copyright (c) 2007-2008 Sonatype, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.sonatype.nexus.index.context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.LockObtainFailedException;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.nexus.index.ArtifactInfo;

public class IndexUtils
{
    public static final String TIMESTAMP_FILE = "timestamp";

    // timestamp

    public static ArtifactInfo constructArtifactInfo( Document doc, IndexingContext context )
    {
        boolean res = false;
    
        ArtifactInfo artifactInfo = new ArtifactInfo();
    
        for ( IndexCreator ic : context.getIndexCreators() )
        {
            res |= ic.updateArtifactInfo( doc, artifactInfo );
        }
    
        return res ? artifactInfo : null;
    }

    public static Document updateDocument( Document doc, IndexingContext context )
    {
        ArtifactInfo ai = constructArtifactInfo( doc, context );
        if ( ai == null )
        {
            return doc;
        }
    
        Document document = new Document();
        
        // unique key
        document.add( new Field( ArtifactInfo.UINFO, ai.getUinfo(), Field.Store.YES, Field.Index.UN_TOKENIZED ) );
    
        document.add( new Field( ArtifactInfo.LAST_MODIFIED, //
            Long.toString( System.currentTimeMillis() ), Field.Store.YES, Field.Index.NO ) );
        
        for ( IndexCreator ic : context.getIndexCreators() )
        {
            ic.updateDocument( ai, document );
        }
    
        return document;
    }

    public static void deleteTimestamp( Directory directory )
        throws IOException
    {
        if ( directory.fileExists( TIMESTAMP_FILE ) )
        {
            directory.deleteFile( TIMESTAMP_FILE );
        }
    }

    public static void updateTimestamp( Directory directory, Date timestamp )
        throws IOException
    {
        synchronized ( directory )
        {
            Date currentTimestamp = getTimestamp( directory );

            if ( timestamp != null && ( currentTimestamp == null || !currentTimestamp.equals( timestamp ) ) )
            {
                deleteTimestamp( directory );

                IndexOutput io = directory.createOutput( TIMESTAMP_FILE );

                try
                {
                    io.writeLong( timestamp.getTime() );

                    io.flush();
                }
                finally
                {
                    close( io );
                }
            }
        }
    }

    public static Date getTimestamp( Directory directory )
    {
        synchronized ( directory )
        {
            Date result = null;
            try
            {
                if ( directory.fileExists( TIMESTAMP_FILE ) )
                {
                    IndexInput ii = null;

                    try
                    {
                        ii = directory.openInput( TIMESTAMP_FILE );

                        result = new Date( ii.readLong() );
                    }
                    finally
                    {
                        close( ii );
                    }
                }
            }
            catch ( IOException ex )
            {
            }

            return result;
        }
    }

    // pack/unpack

    // public static Date getIndexArchiveTime( InputStream is )
    // throws IOException
    // {
    // ZipInputStream zis = null;
    // try
    // {
    // zis = new ZipInputStream( is );
    //
    // long timestamp = -1;
    //
    // ZipEntry entry;
    // while ( ( entry = zis.getNextEntry() ) != null )
    // {
    // if ( entry.getName() == IndexUtils.TIMESTAMP_FILE )
    // {
    // return new Date( new DataInputStream( zis ).readLong() );
    // }
    // timestamp = entry.getTime();
    // }
    //
    // return timestamp == -1 ? null : new Date( timestamp );
    // }
    // finally
    // {
    // close( zis );
    // close( is );
    // }
    // }

    /**
     * Unpack legacy index archive into a specified Lucene <code>Directory</code>
     * 
     * @param is a <code>ZipInputStream</code> with index data
     * @param directory Lucene <code>Directory</code> to unpack index data to
     * @return {@link Date} of the index update or null if it can't be read
     */
    public static Date unpackIndexArchive( InputStream is, Directory directory, IndexingContext context )
        throws IOException
    {
        File indexArchive = File.createTempFile( "nexus-index", "" );

        File indexDir = new File( indexArchive.getAbsoluteFile().getParentFile(), indexArchive.getName() + ".dir" );

        indexDir.mkdirs();

        FSDirectory fdir = FSDirectory.getDirectory( indexDir );

        try
        {
            unpackDirectory( fdir, is );
            copyUpdatedDocuments( fdir, directory, context );

            Date timestamp = getTimestamp( fdir );
            updateTimestamp( directory, timestamp );
            return timestamp;
        }
        finally
        {
            close( fdir );
            indexArchive.delete();
            delete( indexDir );
        }
    }

    private static void unpackDirectory( Directory directory, InputStream is )
        throws IOException
    {
        byte[] buf = new byte[4096];

        ZipEntry entry;

        ZipInputStream zis = null;

        try
        {
            zis = new ZipInputStream( is );

            while ( ( entry = zis.getNextEntry() ) != null )
            {
                if ( entry.isDirectory() || entry.getName().indexOf( '/' ) > -1 )
                {
                    continue;
                }

                IndexOutput io = directory.createOutput( entry.getName() );
                try
                {
                    int n = 0;

                    while ( ( n = zis.read( buf ) ) != -1 )
                    {
                        io.writeBytes( buf, n );
                    }
                }
                finally
                {
                    close( io );
                }
            }
        }
        finally
        {
            close( zis );
        }
    }

    private static void copyUpdatedDocuments( Directory sourcedir, Directory targetdir, IndexingContext context )
        throws CorruptIndexException,
            LockObtainFailedException,
            IOException
    {
        IndexWriter w = null;
        IndexReader r = null;
        try
        {
            r = IndexReader.open( sourcedir );
            w = new IndexWriter( targetdir, false, new NexusAnalyzer(), true );

            for ( int i = 0; i < r.maxDoc(); i++ )
            {
                if ( !r.isDeleted( i ) )
                {
                    w.addDocument( IndexUtils.updateDocument( r.document( i ), context ) );
                }
            }

            w.optimize();
            w.flush();
        }
        finally
        {
            close( w );
            close( r );
        }
    }
    
    /**
     * Used to rebuild group information, for example on context which were merged, since merge() of contexts 
     * only merges the Documents with UINFO record (Artifacts).
     */
    public static void rebuildGroups( IndexingContext context ) throws IOException 
    {
        IndexReader r = context.getIndexReader();

        Set<String> rootGroups = new LinkedHashSet<String>();
        Set<String> allGroups = new LinkedHashSet<String>();

        int numDocs = r.maxDoc();

        for ( int i = 0; i < numDocs; i++ )
        {
            if ( r.isDeleted( i ) )
            {
                continue;
            }

            Document d = r.document( i );

            String uinfo = d.get( ArtifactInfo.UINFO );

            if ( uinfo != null )
            {
                ArtifactInfo info = IndexUtils.constructArtifactInfo( d, context );
                rootGroups.add( info.getRootGroup() );
                allGroups.add( info.groupId );
            }
        }

        setRootGroups( context, rootGroups );
        setAllGroups( context, allGroups );

        context.getIndexWriter().optimize();
        context.getIndexWriter().flush();
    }

    // ----------------------------------------------------------------------------
    // Root groups
    // ----------------------------------------------------------------------------

    public static Set<String> getRootGroups( IndexingContext context )
        throws IOException
    {
        return getGroups( context, ArtifactInfo.ROOT_GROUPS, ArtifactInfo.ROOT_GROUPS_VALUE, ArtifactInfo.ROOT_GROUPS_LIST );
    }

    public static void setRootGroups( IndexingContext context, Collection<String> groups )
        throws IOException
    {
        setGroups( context, groups, ArtifactInfo.ROOT_GROUPS, ArtifactInfo.ROOT_GROUPS_VALUE, ArtifactInfo.ROOT_GROUPS_LIST );
    }

    // ----------------------------------------------------------------------------
    // All groups
    // ----------------------------------------------------------------------------

    public static Set<String> getAllGroups( IndexingContext context )
        throws IOException
    {
        return getGroups( context, ArtifactInfo.ALL_GROUPS, ArtifactInfo.ALL_GROUPS_VALUE, ArtifactInfo.ALL_GROUPS_LIST );
    }

    public static void setAllGroups( IndexingContext context, Collection<String> groups )
        throws IOException
    {
        setGroups( context, groups, ArtifactInfo.ALL_GROUPS, ArtifactInfo.ALL_GROUPS_VALUE, ArtifactInfo.ALL_GROUPS_LIST );
    }
    
    private static Set<String> getGroups( IndexingContext context, String field, String filedValue, String listField )
        throws IOException,
            CorruptIndexException
    {
        Hits hits = context.getIndexSearcher().search( new TermQuery( new Term( field, filedValue ) ) );
        Set<String> groups = new LinkedHashSet<String>( Math.max( 10, hits.length() ) );
        if ( hits.length() > 0 )
        {
            Document doc = hits.doc( 0 );
    
            String groupList = doc.get( listField );
    
            if ( groupList != null )
            {
                groups.addAll( Arrays.asList( groupList.split( "\\|" ) ) );
            }
        }
    
        return groups;
    }

    static void setGroups( IndexingContext context, Collection<String> groups, String groupField, String groupFieldValue, String groupListField )
        throws IOException,
            CorruptIndexException
    {
        IndexWriter w = context.getIndexWriter();
    
        w.updateDocument( new Term( groupField, groupFieldValue ), createGroupsDocument(
            groups,
            groupField,
            groupFieldValue,
            groupListField ) );
    
        w.flush();
    }
    
    static Document createGroupsDocument( Collection<String> groups, String field, String fieldValue, String listField )
    {
        Document groupDoc = new Document();
    
        groupDoc.add( new Field( field, //
            fieldValue,
            Field.Store.YES,
            Field.Index.UN_TOKENIZED ) );
    
        groupDoc.add( new Field( listField, //
            ArtifactInfo.lst2str( groups ),
            Field.Store.YES,
            Field.Index.NO ) );
    
        return groupDoc;
    }
    

    // close helpers

    public static void close( OutputStream os )
    {
        if ( os != null )
        {
            try
            {
                os.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    public static void close( InputStream is )
    {
        if ( is != null )
        {
            try
            {
                is.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    public static void close( IndexOutput io )
    {
        if ( io != null )
        {
            try
            {
                io.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    public static void close( IndexInput in )
    {
        if ( in != null )
        {
            try
            {
                in.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    public static void close( IndexReader r )
    {
        if ( r != null )
        {
            try
            {
                r.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    public static void close( IndexWriter w )
    {
        if ( w != null )
        {
            try
            {
                w.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    public static void close( Directory d )
    {
        if ( d != null )
        {
            try
            {
                d.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }
    }

    public static void delete( File indexDir )
    {
        try
        {
            FileUtils.deleteDirectory( indexDir );
        }
        catch ( IOException ex )
        {
            // ignore
        }
    }

}
