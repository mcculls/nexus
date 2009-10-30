/**
 * Sonatype Nexus (TM) Professional Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions/.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.rest;

import org.sonatype.nexus.index.treeview.DefaultTreeNode;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias( "procurementTreeViewResponse" )
public class IndexTreeViewResponseDTO
{
    private DefaultTreeNode data;

    public IndexTreeViewResponseDTO()
    {
    }

    public IndexTreeViewResponseDTO( DefaultTreeNode node )
    {
        this.data = node;
    }

    public DefaultTreeNode getData()
    {
        return data;
    }

    public void setData( DefaultTreeNode data )
    {
        this.data = data;
    }
}
