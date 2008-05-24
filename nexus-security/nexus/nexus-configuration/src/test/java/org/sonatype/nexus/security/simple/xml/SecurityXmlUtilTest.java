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
package org.sonatype.nexus.security.simple.xml;

import junit.framework.TestCase;

import java.net.URL;

public class SecurityXmlUtilTest extends TestCase
{
    public void testRead() throws Exception
    {
        URL resource = getClass().getClassLoader().getResource( "META-INF/nexus/security.xml" );
        assertNotNull( "resource is null", resource );
        SecurityType securityType = SecurityXmlUtil.readSecurity( resource );

        assertEquals( 2, securityType.getUsers().size() );
        assertEquals( 2, securityType.getRoles().size() );
        assertEquals( 4, securityType.getPermissions().size() );

    }
}
