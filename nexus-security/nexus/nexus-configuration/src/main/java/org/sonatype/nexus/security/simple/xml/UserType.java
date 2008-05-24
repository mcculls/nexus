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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userType", propOrder = {
    "userName",
    "password",
    "roles"
    })
public class UserType
{
    @XmlID
    @XmlElement(name = "user-name", required = true)
    private String userName;

    private String password;

    @XmlIDREF
    @XmlElement(name = "role")
    private final Set<RoleType> roles = new LinkedHashSet<RoleType>();

    public UserType()
    {
    }

    public UserType( String userName, String password )
    {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public Set<RoleType> getRoles()
    {
        return roles;
    }

    public void addRole( RoleType role )
    {
        roles.add( role );
    }

    public void removeRole( RoleType role )
    {
        roles.remove( role );
    }

    public String toString()
    {
        return "[User: userName=" + userName + ", roles=" + roles + "]";
    }
}
