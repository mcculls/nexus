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

import org.sonatype.nexus.security.simple.SimpleSecurity;
import org.sonatype.nexus.security.simple.xml.SecurityType;
import org.sonatype.nexus.security.simple.xml.SecurityXmlUtil;

import java.security.Permission;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleAuthorizationSource implements AuthorizationSource
{
    private final AtomicReference<SimpleSecurity> simpleSecurity = new AtomicReference<SimpleSecurity>();
    private final ClassLoader classLoader;
    private SecurityType securityType;

    public SimpleAuthorizationSource(SecurityType securityType, ClassLoader classLoader)
    {
        SimpleSecurity simpleSecurity = SecurityXmlUtil.toSimpleSecurity( securityType, classLoader );
        this.simpleSecurity.set(simpleSecurity);
        this.classLoader = classLoader;
        this.securityType = securityType;
    }

    public synchronized SecurityType getSecurityType() {
        return securityType;
    }

    public synchronized void setSecurityType(SecurityType securityType) {
        SimpleSecurity simpleSecurity = SecurityXmlUtil.toSimpleSecurity( securityType, classLoader );
        this.simpleSecurity.set( simpleSecurity );
        this.securityType = securityType;
    }

    public boolean check( User user, Permission permission )
    {
        return simpleSecurity.get().check( user, permission );
    }

    public boolean check( String roleName, Permission permission )
    {
        return simpleSecurity.get().check( roleName, permission );
    }
}
