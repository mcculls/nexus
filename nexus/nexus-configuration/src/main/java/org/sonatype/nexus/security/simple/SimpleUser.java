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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An immutable user.
 */
public class SimpleUser
{
    private final String userName;
    private final String password;
    private final Set<SimpleRole> roles;

    public SimpleUser( String userName, String password, Set<SimpleRole> roles )
    {
        if ( userName == null )
        {
            throw new NullPointerException( "userName is null" );
        }

        this.userName = userName;
        this.password = password;

        if ( roles != null )
        {
            this.roles = Collections.unmodifiableSet( new LinkedHashSet<SimpleRole>( roles ) );
        }
        else
        {
            this.roles = Collections.emptySet();
        }
    }

    public String getUserName()
    {
        return userName;
    }

    public synchronized boolean isPassword( String password )
    {
        return this.password.equals( password );
    }

    public Set<SimpleRole> getRoles()
    {
        return roles;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder( "[SimpleUser: userName=" ).append( userName ).append( "]" );
        return b.toString();
    }
}
