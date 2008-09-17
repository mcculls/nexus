package org.sonatype.nexus.jsecurity.locators;

import org.sonatype.jsecurity.locators.EmailConfigurationLocator;
import org.sonatype.micromailer.EmailerConfiguration;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.configuration.model.CSmtpConfiguration;

/**
 * @plexus.component
 */
public class NexusEmailConfigurationLocator
    implements
    EmailConfigurationLocator
{
    /**
     * @plexus.requirement
     */
    private NexusConfiguration configuration;
    
    public EmailerConfiguration getConfiguration()
    {
        EmailerConfiguration config = new EmailerConfiguration();
        
        CSmtpConfiguration smtp = configuration.readSmtpConfiguration();
        
        config.setMailHost( smtp.getHost() );
        config.setMailPort( smtp.getPort() );
        config.setDebug( smtp.isDebugMode() );
        config.setUsername( smtp.getUsername() );
        config.setPassword( smtp.getPassword() );
        config.setSsl( smtp.isSslEnabled() );
        config.setTls( smtp.isTlsEnabled() );

        return config;
    }

}
