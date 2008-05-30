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
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public final class RestPermission extends Permission
{
    private final Set<String> includes;
    private final Collection<Pattern> includePatterns;
    private final Set<String> excludes;
    private final Collection<Pattern> excludePatterns;
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
                action = action.trim();
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
        this( name, null, null, Verb.parse( actions.split( "\\s*,\\s*" ) ) );
    }

    public RestPermission( Set<String> includes, Set<String> excludes, Set<Verb> verbs ) throws IllegalArgumentException
    {
        this( null, includes, excludes, verbs);
    }

    private  RestPermission( String name, Set<String> includes, Set<String> excludes, Set<Verb> verbs ) throws IllegalArgumentException
    {
        super( name != null ? name : buildName( includes, excludes) );

        if (name != null && includes == null && excludes == null) {
            includes = new LinkedHashSet<String>();
            excludes = new LinkedHashSet<String>();
            for ( String pattern : name.split( "\\s*:\\s*" ))
            {
                if (pattern.charAt( 0 ) == '!' ) {
                    excludes.add(pattern.substring( 1 ).trim());
                } else {
                    includes.add(pattern.trim());
                }
            }
        }


        // calculate hashCode as we build

        // includes
        Collection<Pattern> includePatterns = new ArrayList<Pattern>( includes.size());
        for ( String include : includes )
        {
            includePatterns.add(Pattern.compile( include ));
        }
        this.includes = Collections.unmodifiableSet( includes );
        this.includePatterns = Collections.unmodifiableCollection( includePatterns );

        // excludes
        Collection<Pattern> excludePatterns = new ArrayList<Pattern>(excludes.size());
        for ( String exclude : excludes )
        {
            excludePatterns.add(Pattern.compile( exclude ));
        }
        this.excludes = Collections.unmodifiableSet( excludes );
        this.excludePatterns = Collections.unmodifiableCollection( excludePatterns );

        // verbs
        this.verbs = Collections.unmodifiableSet( EnumSet.copyOf( verbs ) );

        // hashCode
        int hashCode;
        hashCode = includes.hashCode();
        hashCode = 31 * hashCode + excludes.hashCode();
        hashCode = 31 * hashCode + verbs.hashCode();
        this.hashCode = hashCode;
    }

    public boolean implies( Permission permission )
    {
        if ( permission == null || !( permission instanceof RestPermission ) )
        {
            return false;
        }

        RestPermission other = (RestPermission) permission;

        // does this permission have all of the desired verb?
        if (!verbs.containsAll( other.verbs )) {
            return false;
        }

        // does this permission allow all of the desired URIs?
        for ( Pattern test : other.includePatterns )
        {
            if ( !isAllowed( test.pattern() )) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllowed(String uri) {
        boolean allowed = false;

        // is the uri in the includes set?
        for ( Pattern include : includePatterns )
        {
            if ( include.matcher( uri ).matches() ) {
                allowed = true;
                break;
            }
        }

        if ( allowed )
        {
            // is the not uri in the excludes set?
            for ( Pattern exclude : excludePatterns )
            {
                if ( exclude.matcher( uri ).matches() ) {
                    allowed = false;
                    break;
                }
            }
        }

        return allowed;
    }

    public Set<String> getIncludes()
    {
        return includes;
    }

    public Set<String> getExcludes()
    {
        return excludes;
    }

    public Set<Verb> getVerbs()
    {
        return verbs;
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
        return includes.equals( other.includes ) &&
            excludes.equals( other.excludes ) &&
            verbs.equals( other.verbs );
    }

    public int hashCode()
    {
        return hashCode;
    }

    private static String buildName( Collection<String> includes, Collection<String> excludes )
    {
        StringBuilder name = new StringBuilder();
        if (includes != null) {
            for ( String include : includes )
            {
                if (name.length() > 0) name.append(":");
                name.append(include);
            }
        }
        if (excludes != null) {
            for ( String exclude : excludes )
            {
                if (name.length() > 0) name.append(":");
                name.append( "!" ).append(exclude);
            }
        }
        return name.toString();
    }
}