package org.sonatype.nexus.jsecurity;

import org.sonatype.jsecurity.realms.tools.ConfigurationManager;
import org.sonatype.jsecurity.realms.tools.NoSuchUserException;
import org.sonatype.nexus.NexusService;

public interface NexusSecurity
    extends ConfigurationManager, NexusService
{
    String ROLE = NexusSecurity.class.getName();
    
    void forgotPassword( String userId, String email )
        throws NoSuchUserException,
            NoSuchEmailException;
    
    void forgotUsername( String email )
        throws NoSuchEmailException;
    
    void resetPassword( String userId )
        throws NoSuchUserException;
    
    void changePassword( String userId, String oldPassword, String newPassword )
        throws NoSuchUserException,
            InvalidCredentialsException;
}
