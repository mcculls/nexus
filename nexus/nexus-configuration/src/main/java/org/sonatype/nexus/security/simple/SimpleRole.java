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
package org.sonatype.nexus.security.simple;

import java.security.Permission;
import java.security.Permissions;
import java.util.Collections;

/** An immutable role. */
public class SimpleRole
{
    private final String roleName;
    private final Permissions permissions;

    public SimpleRole( String roleName, Permissions permissions )
    {
        if ( roleName == null )
        {
            throw new NullPointerException( "roleName is null" );
        }
        if ( permissions == null )
        {
            throw new NullPointerException( "permissions is null" );
        }

        this.roleName = roleName;
        this.permissions = new Permissions();
        for ( Permission permission : Collections.list( permissions.elements() ) )
        {
            this.permissions.add( permission );
        }
        permissions.setReadOnly();
    }

    public String getRoleName()
    {
        return roleName;
    }

    public boolean implies( Permission permission )
    {
        if ( permission == null )
        {
            throw new NullPointerException( "permission is null" );
        }

        return permissions.implies( permission );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        SimpleRole role = (SimpleRole) o;

        return roleName.equals( role.roleName ) && permissions.equals( role.permissions );
    }

    public int hashCode()
    {
        int result;
        result = roleName.hashCode();
        result = 31 * result + permissions.hashCode();
        return result;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder( "[SimpleRole: roleName=" ).append( roleName ).append( "]" );
        return b.toString();
    }

}
