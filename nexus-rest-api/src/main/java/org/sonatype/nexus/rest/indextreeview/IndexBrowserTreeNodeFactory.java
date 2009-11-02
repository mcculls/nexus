package org.sonatype.nexus.rest.indextreeview;

import org.sonatype.nexus.index.ArtifactInfo;
import org.sonatype.nexus.index.context.IndexingContext;
import org.sonatype.nexus.index.treeview.DefaultMergedTreeNodeFactory;
import org.sonatype.nexus.index.treeview.IndexTreeView;
import org.sonatype.nexus.index.treeview.TreeNode;
import org.sonatype.nexus.proxy.repository.Repository;

public class IndexBrowserTreeNodeFactory
    extends DefaultMergedTreeNodeFactory
{
    public IndexBrowserTreeNodeFactory( IndexingContext ctx, Repository repository )
    {
        super( ctx, repository );
    }

    @Override
    protected TreeNode decorateArtifactNode( IndexTreeView tview, ArtifactInfo ai, String path, TreeNode node )
    {
        IndexBrowserTreeNode iNode = ( IndexBrowserTreeNode ) super.decorateArtifactNode( tview, ai, path, node );
        
        iNode.setClassifier( ai.classifier );
        iNode.setExtension( ai.fextension );
        iNode.setArtifactUri( buildArtifactUri() );
        iNode.setPomUri( buildPomUri() );

        return iNode;
    }

    @Override
    protected TreeNode instantiateNode( IndexTreeView tview, String path, boolean leaf, String nodeName )
    {
        return new IndexBrowserTreeNode( tview, this );
    }
    
    protected String buildArtifactUri()
    {
        return null;
    }
    
    protected String buildPomUri()
    {
        return null;
    }
}
