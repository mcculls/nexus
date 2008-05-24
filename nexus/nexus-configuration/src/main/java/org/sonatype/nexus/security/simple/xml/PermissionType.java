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
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "permissionType", propOrder = {
    "permissionId",
    "clazz",
    "name",
    "actions"
    })
public class PermissionType
{
    @XmlID
    @XmlElement(name = "permission-id", required = true)
    protected String permissionId;

    @XmlElement(name = "class", required = true)
    protected String clazz;

    @XmlElement(required = true)
    protected String name;

    @XmlElement(required = true)
    protected String actions;

    public PermissionType()
    {
    }

    public PermissionType( String permissionId, String clazz, String name, String actions )
    {
        this.permissionId = permissionId;
        this.clazz = clazz;
        this.name = name;
        this.actions = actions;
    }

    public String getPermissionId()
    {
        return permissionId;
    }

    public void setPermissionId( String permissionId )
    {
        this.permissionId = permissionId;
    }

    public String getClazz()
    {
        return clazz;
    }

    public void setClazz( String clazz )
    {
        this.clazz = clazz;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getActions()
    {
        return actions;
    }

    public void setActions( String actions )
    {
        this.actions = actions;
    }

    public String toString()
    {
        return "[Permission: permissionId=" + permissionId + "]";
    }
}
