package org.sonatype.nexus.restlight.core;

import java.util.ArrayList;
import java.util.List;

public class Role
{
    private String resourceURI;

    private String id;

    private String name;

    private String description;

    private int sessionTimeout;

    private boolean userManaged = true;

    private List<String> roles = new ArrayList<String>();

    private List<String> privileges = new ArrayList<String>();

    public String getResourceURI()
    {
        return resourceURI;
    }

    public void setResourceURI( final String resourceURI )
    {
        this.resourceURI = resourceURI;
    }

    public String getId()
    {
        return id;
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public int getSessionTimeout()
    {
        return sessionTimeout;
    }

    public void setSessionTimeout( final int sessionTimeout )
    {
        this.sessionTimeout = sessionTimeout;
    }

    public boolean isUserManaged()
    {
        return userManaged;
    }

    public void setUserManaged( final boolean userManaged )
    {
        this.userManaged = userManaged;
    }

    public List<String> getPrivileges()
    {
        return privileges;
    }

    public void setPrivileges( final List<String> privileges )
    {
        this.privileges = privileges;
    }

    public List<String> getRoles()
    {
        return roles;
    }

    public void setRoles( final List<String> roles )
    {
        this.roles = roles;
    }

    @Override
    public String toString()
    {
        return "Role [description=" + description + "\n  id=" + id + "\n  name=" + name + "\n  privileges="
            + privileges + "\n  resourceURI=" + resourceURI + "\n  roles=" + roles + "\n  sessionTimeout="
            + sessionTimeout + "\n  userManaged=" + userManaged + "]";
    }

}
