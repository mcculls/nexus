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

import junit.framework.TestCase;

public class RestPermissionTest extends TestCase
{
    public void testSimplePermission()
    {
        RestPermission permission = new RestPermission( "/simple/.*", "GET" );

        assertImplies( permission, new RestPermission( "/simple/TEST", "GET" ) );
        assertImplies( permission, new RestPermission( "/simple/", "GET" ) );

        assertNotImplies( permission, new RestPermission( "/unknown", "GET" ) );

        // regular expression will not match without trailing '/'
        assertNotImplies( permission, new RestPermission( "/simple", "GET" ) );

        // regular expression will not match without leading '/'
        assertNotImplies( permission, new RestPermission( "simple", "GET" ) );
    }

    public void testComplexPermission()
    {
        RestPermission permission = new RestPermission( ".*/org/apache/.*", "GET" );

        assertImplies( permission, new RestPermission( "/repository/org/apache/openejb", "GET" ) );
        assertImplies( permission, new RestPermission( "/repository/org/apache/", "GET" ) );
        assertImplies( permission, new RestPermission( "/org/apache/", "GET" ) );

        assertNotImplies( permission, new RestPermission( "/unknown", "GET" ) );

        // regular expression will not match with trailing '/'
        assertNotImplies( permission, new RestPermission( "/repository/org/apache", "GET" ) );
    }

    public void testSingleVerb()
    {
        RestPermission permission = new RestPermission( "TEST", "GET" );

        assertImplies( permission, new RestPermission( "TEST", "GET" ) );
        assertNotImplies( permission, new RestPermission( "TEST", "POST" ) );
    }

    public void testMultipleVerbs()
    {
        RestPermission permission = new RestPermission( "TEST", "GET,POST" );

        assertImplies( permission, new RestPermission( "TEST", "GET" ) );
        assertImplies( permission, new RestPermission( "TEST", "POST" ) );
        assertImplies( permission, new RestPermission( "TEST", "GET,POST" ) );
        assertNotImplies( permission, new RestPermission( "TEST", "DELETE" ) );
        assertNotImplies( permission, new RestPermission( "TEST", "GET,DELETE" ) );
        assertNotImplies( permission, new RestPermission( "TEST", "GET,POST,DELETE" ) );
    }

    public void testInvalidPermission() {
        assertInvalidPermission( "*INVALID", "GET" );
        assertInvalidPermission( "VALID", "UNKNOWN" );
        assertInvalidPermission( "VALID", "GET,UNKNOWN" );
        
        // white space is not allowed in actions (we can change this)
        assertInvalidPermission( "VALID", "GET, POST" );
    }

    public static void assertImplies( RestPermission pattern, RestPermission test )
    {
        if ( !pattern.implies( test ) )
        {
            fail( "Expected " + pattern + " to imply " + test );
        }
    }

    public static void assertNotImplies( RestPermission pattern, RestPermission test )
    {
        if ( pattern.implies( test ) )
        {
            fail( "Expected " + pattern +" to not imply " + test );
        }
    }

    private void assertInvalidPermission( String uriPattern, String verbs )
    {
        try
        {
            new RestPermission( uriPattern, verbs );
            fail("Expected IllegalArgumentException from new RestPermission( \"" + uriPattern + "\", \"" + verbs + "\")" );
        }
        catch ( IllegalArgumentException expected )
        {
        }
    }
}
