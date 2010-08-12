/**
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.security.realms.simple;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.codehaus.plexus.component.annotations.Component;

/**
 * All this class really needs to do is return an AuthorizationInfo. You could go go all out and implement Realm, but
 * that is more then I want to cover in this example.
 */
@Component( role = Realm.class, hint = "Simple", description = "Simple In Memory Realm" )
// The role must be Realm.class, and the hint is up to you.
public class SimpleRealm
    extends AuthorizingRealm
{
    /**
     * This is a very simple in memory user Store.
     */
    private UserStore userStore = new UserStore();

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo( PrincipalCollection principals )
    {
        // Unless your realm is very specific the XmlAuthorizingRealm will take
        // care of this. (provided you implement the PlexusUserLocator interface).
        String username = principals.getPrimaryPrincipal().toString();
        final SimpleUser user = this.userStore.getUser( username );
        if ( user != null )
        {
            return new SimpleAuthorizationInfo( user.getRoles() );
        }
        else
        {
            return null;
        }

    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo( AuthenticationToken token )
        throws AuthenticationException
    {
        // all we need to do here is look up the user by id, in the user store, and return a AuthenticationInfo with the
        // real users id and pass.

        // type check the token
        if ( !UsernamePasswordToken.class.isAssignableFrom( token.getClass() ) )
        {
            return null;
        }
        String userId = ( (UsernamePasswordToken) token ).getUsername();

        // look the user in the example user store
        SimpleUser user = this.userStore.getUser( userId );

        if ( user == null )
        {
            throw new AuthenticationException( "Invalid username '" + userId + "'" );
        }

        return new SimpleAuthenticationInfo( user.getUserId(), user.getPassword(), getName() );
    }

}
