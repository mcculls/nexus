/**
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.proxy.storage.local;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.inject.Singleton;

import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.LocalStorageException;
import org.sonatype.nexus.proxy.ResourceStoreIteratorRequest;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.AbstractStorageItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.UnsupportedStorageOperationException;
import org.sonatype.plugin.ExtensionPoint;

/**
 * Local storage.
 * 
 * @author cstamas
 */
@ExtensionPoint
@Singleton
public interface LocalRepositoryStorage
{
    /**
     * Returns a designator to identify the local storage implementation (hint: for example the Plexus role hint).
     * 
     * @return
     */
    String getProviderId();

    /**
     * Validate that the URL that defines storage location is valid.
     * 
     * @param url
     * @throws LocalStorageException
     */
    void validateStorageUrl( String url )
        throws LocalStorageException;

    /**
     * Check local storage for reachability.
     * 
     * @param uid the uid
     * @return true, if available (reachable)
     * @throws LocalStorageException the storage exception
     */
    boolean isReachable( Repository repository, ResourceStoreRequest request )
        throws LocalStorageException;

    /**
     * Gets the absolute url from base.
     * 
     * @deprecated This is for internal use only!
     * @param uid the uid
     * @return the absolute url from base
     */
    URL getAbsoluteUrlFromBase( Repository repository, ResourceStoreRequest request )
        throws LocalStorageException;

    /**
     * Contains item.
     * 
     * @param uid the uid
     * @return true, if successful
     * @throws LocalStorageException the storage exception
     */
    boolean containsItem( Repository repository, ResourceStoreRequest request )
        throws LocalStorageException;

    /**
     * Retrieve item.
     * 
     * @param uid the uid
     * @return the abstract storage item
     * @throws ItemNotFoundException the item not found exception
     * @throws LocalStorageException the storage exception
     */
    AbstractStorageItem retrieveItem( Repository repository, ResourceStoreRequest request )
        throws ItemNotFoundException, LocalStorageException;

    /**
     * Store item.
     * 
     * @param item the item
     * @throws UnsupportedStorageOperationException the unsupported storage operation exception
     * @throws LocalStorageException the storage exception
     */
    void storeItem( Repository repository, StorageItem item )
        throws UnsupportedStorageOperationException, LocalStorageException;

    /**
     * Delete item, using wastebasket.
     * 
     * @param uid the uid
     * @throws ItemNotFoundException the item not found exception
     * @throws UnsupportedStorageOperationException the unsupported storage operation exception
     * @throws LocalStorageException the storage exception
     */
    void deleteItem( Repository repository, ResourceStoreRequest request )
        throws ItemNotFoundException, UnsupportedStorageOperationException, LocalStorageException;

    /**
     * Shred item, avoid wastebasket.
     * 
     * @param uid the uid
     * @throws ItemNotFoundException the item not found exception
     * @throws UnsupportedStorageOperationException the unsupported storage operation exception
     * @throws LocalStorageException the storage exception
     */
    void shredItem( Repository repository, ResourceStoreRequest request )
        throws ItemNotFoundException, UnsupportedStorageOperationException, LocalStorageException;

    /**
     * Move item from path to path.
     * 
     * @param repository
     * @param from
     * @param to
     * @throws ItemNotFoundException
     * @throws UnsupportedStorageOperationException
     * @throws LocalStorageException
     */
    void moveItem( Repository repository, ResourceStoreRequest from, ResourceStoreRequest to )
        throws ItemNotFoundException, UnsupportedStorageOperationException, LocalStorageException;

    /**
     * List items.
     * 
     * @param uid the uid
     * @return the collection< storage item>
     * @throws ItemNotFoundException the item not found exception
     * @throws UnsupportedStorageOperationException the unsupported storage operation exception
     * @throws LocalStorageException the storage exception
     */
    Collection<StorageItem> listItems( Repository repository, ResourceStoreRequest request )
        throws ItemNotFoundException, LocalStorageException;

    /**
     * Iterate over items.
     * 
     * @param repository
     * @param request
     * @return
     * @throws ItemNotFoundException
     * @throws LocalStorageException
     */
    Iterator<StorageItem> iterateItems( Repository repository, ResourceStoreIteratorRequest request )
        throws ItemNotFoundException, LocalStorageException;
}
