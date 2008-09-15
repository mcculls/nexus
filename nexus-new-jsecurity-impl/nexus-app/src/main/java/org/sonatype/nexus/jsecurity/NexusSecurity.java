package org.sonatype.nexus.jsecurity;

import org.sonatype.jsecurity.realms.tools.ConfigurationManager;
import org.sonatype.nexus.NexusService;

public interface NexusSecurity
    extends ConfigurationManager, NexusService
{
    String ROLE = NexusSecurity.class.getName();
}
