package org.sonatype.nexus.integrationtests.upgrades.nexus652;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.sonatype.nexus.configuration.model.Configuration;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.test.utils.NexusConfigUtil;
import org.sonatype.nexus.test.utils.SecurityConfigUtil;
import org.sonatype.nexus.test.utils.TestProperties;

/**
 * Test nexus.xml after and upgrade from 1.0.0-beta-5 to 1.0.0.
 */
public class Nexus652Beta5To10UpgradeTest
    extends AbstractNexusIntegrationTest
{

    public Nexus652Beta5To10UpgradeTest()
    {
        this.setVerifyNexusConfigBeforeStart( false );
        
        TestContainer.getInstance().getTestContext().setSecureTest( true );
    }

    @Test
    public void checkNexusConfig()
        throws IOException
    {
        // if we made it this far the upgrade worked...

        Configuration nexusConfig = NexusConfigUtil.getNexusConfig();

        Assert.assertEquals( "foo.org", nexusConfig.getSmtpConfiguration().getHost(), "Smtp host:" );
        Assert.assertEquals( "now", nexusConfig.getSmtpConfiguration().getPassword(), "Smtp password:" );
        Assert.assertEquals( "void", nexusConfig.getSmtpConfiguration().getUsername(), "Smtp username:" );
        Assert.assertEquals( 465, nexusConfig.getSmtpConfiguration().getPort(), "Smtp port:" );

        Assert.assertEquals( "User3", nexusConfig.getSecurity().getAnonymousUsername(), "Security anon username:" );
        Assert.assertEquals( "y6i0t9q1e3", nexusConfig.getSecurity().getAnonymousPassword(), "Security anon password:" );
        Assert.assertEquals( true, nexusConfig.getSecurity().isAnonymousAccessEnabled(), "Security anon access:" );
        Assert.assertEquals( true, nexusConfig.getSecurity().isEnabled(), "Security enabled:" );
        Assert.assertEquals( 3, nexusConfig.getSecurity().getRealms().size(), "Security realm size:" );
        Assert.assertEquals( "XmlAuthenticatingRealm", nexusConfig.getSecurity().getRealms().get( 0 ), "Security realm:" );
        Assert.assertEquals( "NexusMethodAuthorizingRealm", nexusConfig.getSecurity().getRealms().get( 1 ),"Security realm:" );
        Assert.assertEquals( "NexusTargetAuthorizingRealm", nexusConfig.getSecurity().getRealms().get( 2 ), "Security realm:" );

        Assert.assertEquals( true, nexusConfig.getHttpProxy().isEnabled(), "http proxy:" );

        Assert.assertEquals( TestProperties.getString( "nexus.base.url" ),
                             nexusConfig.getRestApi().getBaseUrl(), "Base url:" );

        // we will glance over the repos, because the unit tests cover this.
        Assert.assertEquals( 6, nexusConfig.getRepositories().size(),"Repository Count:" );
        Assert.assertEquals( 1, nexusConfig.getRepositoryShadows().size(),"Repository Shadow Count:" );

        Assert.assertNotNull( NexusConfigUtil.getRepo( "central" ), "repo: central" );
        Assert.assertNotNull( NexusConfigUtil.getRepo( "apache-snapshots" ), "repo: apache-snapshots" );
        Assert.assertNotNull( NexusConfigUtil.getRepo( "codehaus-snapshots" ), "repo: codehaus-snapshots" );
        Assert.assertNotNull( NexusConfigUtil.getRepo( "releases" ), "repo: releases" );
        Assert.assertNotNull( NexusConfigUtil.getRepo( "snapshots" ),"repo: snapshots" );
        Assert.assertNotNull( NexusConfigUtil.getRepo( "thirdparty" ), "repo: thirdparty" );
        
        // everything else including everything above should be covered by unit tests.

    }
    

    @Test
    public void checkSecurityConfig()
        throws IOException
    {
        org.sonatype.jsecurity.model.Configuration secConfig = SecurityConfigUtil.getSecurityConfig();
        
        Assert.assertEquals( 7, secConfig.getUsers().size(), "User Count:");
        Assert.assertEquals( 19, secConfig.getRoles().size(), "Roles Count:");
        
        // again, everything should have been upgraded.
    }

}
