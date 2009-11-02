/**
 * Sonatype Nexus (TM) Professional Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions/.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.rest.indextreeview;

import org.sonatype.nexus.index.treeview.TreeNode;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "procurementTreeViewResponse" )
public class IndexBrowserTreeViewResponseDTO
{
    private TreeNode data;

    public IndexBrowserTreeViewResponseDTO()
    {
    }

    public IndexBrowserTreeViewResponseDTO( TreeNode node )
    {
        this.data = node;
    }

    public TreeNode getData()
    {
        return data;
    }

    public void setData( TreeNode data )
    {
        this.data = data;
    }
}
