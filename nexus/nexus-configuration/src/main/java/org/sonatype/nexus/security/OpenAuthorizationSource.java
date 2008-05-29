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

import java.security.Permission;

/** @plexus.component role="org.sonatype.nexus.security.AuthorizationSource" instantiation-strategy="per-lookup" role-hint="open" */
public class OpenAuthorizationSource implements AuthorizationSource
{
    public boolean check( User user, Permission permission )
    {
        return true;
    }

    public boolean check( String roleName, Permission permission )
    {
        return true;
    }
}
