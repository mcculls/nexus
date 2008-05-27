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
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class RestPermission extends Permission
{
    private final Pattern uriPattern;
    private final Set<Verb> verbs;
    private final int hashCode;

    private static enum Verb
    {
        GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE;

        private static EnumSet<Verb> parse( String... verbs )
        {
            EnumSet<Verb> verbEnumSet = EnumSet.noneOf( Verb.class );
            for ( String action : verbs )
            {
                try
                {
                    Verb verb = Verb.valueOf( action );
                    verbEnumSet.add( verb );
                }
                catch ( IllegalArgumentException e )
                {
                    throw new IllegalArgumentException( "Unknown REST verb " + action );
                }
            }
            return verbEnumSet;
        }
    }


    public RestPermission( String name, String actions ) throws IllegalArgumentException
    {
        this( name, Verb.parse( actions.split( "," ) ) );
    }

    public RestPermission( String uriPattern, String[] verbs ) throws IllegalArgumentException
    {
        this( uriPattern, Verb.parse( verbs ) );
    }

    public RestPermission( String uriPattern, Set<Verb> verbs ) throws IllegalArgumentException
    {
        super( uriPattern );

        this.uriPattern = Pattern.compile( uriPattern );

        this.verbs = Collections.unmodifiableSet( EnumSet.copyOf( verbs ) );

        hashCode = this.uriPattern.pattern().hashCode() ^ verbs.hashCode();
    }

    public boolean implies( Permission permission )
    {
        if ( permission == null || !( permission instanceof RestPermission ) )
        {
            return false;
        }

        RestPermission other = (RestPermission) permission;
        return uriPattern.matcher( other.uriPattern.pattern() ).matches() && verbs.containsAll( other.verbs );
    }

    public String getActions()
    {
        StringBuffer actions = new StringBuffer();
        for ( Verb verb : verbs )
        {
            if ( actions.length() > 0 )
            {
                actions.append( "," );
            }
            actions.append( verb );
        }
        return actions.toString();
    }

    public boolean equals( Object o )
    {
        if ( o == null || !( o instanceof RestPermission ) )
        {
            return false;
        }

        RestPermission other = (RestPermission) o;
        return uriPattern.pattern().equals( other.uriPattern.pattern() ) && verbs.equals( other.verbs );
    }

    public int hashCode()
    {
        return hashCode;
    }
}