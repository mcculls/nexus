/*
 * Nexus: Maven Repository Manager
 * Copyright (C) 2008 Sonatype Inc.                                                                                                                          
 * 
 * This file is part of Nexus.                                                                                                                                  
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */
package org.sonatype.nexus.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

public final class RestPermission extends Permission implements Serializable
{
    private transient int cachedHashCode = 0;
    private transient URLPatternSpec urlPatternSpec;
    private transient HTTPMethodSpec httpMethodSpec;

//    public RestPermission( HttpServletRequest request )
//    {
//        super( URLPatternSpec.encodeColons( request ) );
//
//        urlPatternSpec = new URLPatternSpec( getName() );
//        httpMethodSpec = new HTTPMethodSpec( request.getMethod(), HTTPMethodSpec.NA );
//    }

    public RestPermission( String name, String actions )
    {
        super( name );

        urlPatternSpec = new URLPatternSpec( name );
        httpMethodSpec = new HTTPMethodSpec( actions, false );
    }

    public RestPermission( String urlPattern, String[] HTTPMethods )
    {
        super( urlPattern );

        urlPatternSpec = new URLPatternSpec( urlPattern );
        httpMethodSpec = new HTTPMethodSpec( HTTPMethods );
    }

    public boolean equals( Object o )
    {
        if ( o == null || !( o instanceof RestPermission ) )
        {
            return false;
        }

        RestPermission other = (RestPermission) o;
        return urlPatternSpec.equals( other.urlPatternSpec ) && httpMethodSpec.equals( other.httpMethodSpec );
    }

    public String getActions()
    {
        return httpMethodSpec.getActions();
    }

    public int hashCode()
    {
        if ( cachedHashCode == 0 )
        {
            cachedHashCode = urlPatternSpec.hashCode() ^ httpMethodSpec.hashCode();
        }
        return cachedHashCode;
    }

    public boolean implies( Permission permission )
    {
        if ( permission == null || !( permission instanceof RestPermission ) )
        {
            return false;
        }

        RestPermission other = (RestPermission) permission;
        return urlPatternSpec.implies( other.urlPatternSpec ) && httpMethodSpec.implies( other.httpMethodSpec );
    }

    public PermissionCollection newPermissionCollection()
    {
        return new RestPermissionCollection();
    }

    private synchronized void readObject( ObjectInputStream in ) throws IOException
    {
        urlPatternSpec = new URLPatternSpec( in.readUTF() );
        httpMethodSpec = new HTTPMethodSpec( in.readUTF(), false );
    }

    private synchronized void writeObject( ObjectOutputStream out ) throws IOException
    {
        out.writeUTF( urlPatternSpec.getPatternSpec() );
        out.writeUTF( httpMethodSpec.getActions() );
    }

    private static final class RestPermissionCollection extends PermissionCollection
    {
        private Hashtable<Permission, Permission> permissions = new Hashtable<Permission, Permission>();

        /**
         * Adds a permission object to the current collection of permission objects.
         *
         * @param permission the Permission object to add.
         * @throws SecurityException -  if this PermissionCollection object
         *                           has been marked readonly
         */
        public void add( Permission permission )
        {
            if ( isReadOnly() )
            {
                throw new IllegalArgumentException( "Read only collection" );
            }

            if ( !( permission instanceof RestPermission ) )
            {
                throw new IllegalArgumentException( "Wrong permission type" );
            }

            RestPermission p = (RestPermission) permission;

            permissions.put( p, p );
        }

        /**
         * Checks to see if the specified permission is implied by
         * the collection of Permission objects held in this PermissionCollection.
         *
         * @param permission the Permission object to compare.
         * @return true if "permission" is implied by the  permissions in
         *         the collection, false if not.
         */
        public boolean implies( Permission permission )
        {
            if ( !( permission instanceof RestPermission ) )
            {
                return false;
            }

            RestPermission p = (RestPermission) permission;
            Enumeration e = permissions.elements();

            while ( e.hasMoreElements() )
            {
                if ( ( (RestPermission) e.nextElement() ).implies( p ) )
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * Returns an enumeration of all the Permission objects in the collection.
         *
         * @return an enumeration of all the Permissions.
         */
        public Enumeration<Permission> elements()
        {
            return permissions.elements();
        }
    }
}