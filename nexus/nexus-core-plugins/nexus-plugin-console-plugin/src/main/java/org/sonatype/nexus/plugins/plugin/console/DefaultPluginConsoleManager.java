/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.plugin.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.sonatype.nexus.plugins.NexusPluginManager;
import org.sonatype.nexus.plugins.PluginResponse;
import org.sonatype.nexus.plugins.plugin.console.model.DocumentationLink;
import org.sonatype.nexus.plugins.plugin.console.model.PluginInfo;
import org.sonatype.nexus.plugins.plugin.console.model.RestInfo;
import org.sonatype.nexus.plugins.rest.NexusDocumentationBundle;
import org.sonatype.nexus.plugins.rest.NexusResourceBundle;
import org.sonatype.plexus.rest.resource.PlexusResource;
import org.sonatype.plugin.metadata.GAVCoordinate;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author juven
 */
@Component( role = PluginConsoleManager.class )
public class DefaultPluginConsoleManager
    extends AbstractLogEnabled
    implements PluginConsoleManager, Initializable
{
    @Requirement
    private NexusPluginManager pluginManager;

    @Requirement
    private PlexusContainer plexusContainer;

    @Requirement( role = NexusResourceBundle.class )
    private List<NexusResourceBundle> resourceBundles;

    private Multimap<String, NexusDocumentationBundle> docBundles;

    public void initialize()
        throws InitializationException
    {
        docBundles = new LinkedHashMultimap<String, NexusDocumentationBundle>();

        for ( NexusResourceBundle rb : resourceBundles )
        {
            if ( rb instanceof NexusDocumentationBundle )
            {
                NexusDocumentationBundle doc = (NexusDocumentationBundle) rb;

                docBundles.put( doc.getPluginId(), doc );
            }
        }
    }

    public List<PluginInfo> listPluginInfo()
    {
        List<PluginInfo> result = new ArrayList<PluginInfo>();

        Map<GAVCoordinate, PluginResponse> pluginResponses = pluginManager.getPluginResponses();

        for ( PluginResponse pluginResponse : pluginResponses.values() )
        {
            result.add( buildPluginInfo( pluginResponse ) );
        }

        return result;
    }

    private PluginInfo buildPluginInfo( PluginResponse pluginResponse )
    {
        PluginInfo result = new PluginInfo();

        result.setStatus( pluginResponse.getAchievedGoal().name() );
        result.setVersion( pluginResponse.getPluginCoordinates().getVersion() );
        if ( pluginResponse.getPluginDescriptor() != null )
        {
            result.setName( pluginResponse.getPluginDescriptor().getPluginMetadata().getName() );
            result.setDescription( pluginResponse.getPluginDescriptor().getPluginMetadata().getDescription() );
            result.setScmVersion( pluginResponse.getPluginDescriptor().getPluginMetadata().getScmVersion() );
            result.setScmTimestamp( pluginResponse.getPluginDescriptor().getPluginMetadata().getScmTimestamp() );
            result.setSite( pluginResponse.getPluginDescriptor().getPluginMetadata().getPluginSite() );
        }
        else
        {
            result.setName( pluginResponse.getPluginCoordinates().getGroupId() + ":"
                + pluginResponse.getPluginCoordinates().getArtifactId() );
        }

        Collection<NexusDocumentationBundle> docs =
            docBundles.get( pluginResponse.getPluginCoordinates().getArtifactId() );
        if ( docs != null && !docs.isEmpty() )
        {
            for ( NexusDocumentationBundle bundle : docs )
            {
                // here, we (mis)use the documentation field, to store path segments only, the REST resource will create
                // proper URLs out this these.
                DocumentationLink link = new DocumentationLink();
                link.setLabel( bundle.getDescription() );
                link.setUrl( bundle.getPluginId() + "/" + bundle.getPathPrefix() );
                result.addDocumentation( link );
            }
        }

        if ( !pluginResponse.isSuccessful() )
        {
            result.setFailureReason( pluginResponse.formatAsString( false ) );
        }

        // WARN
        // dirty hack here, the logic here should be moved into PluginManger
        if ( pluginResponse.isSuccessful() )
        {
            List<String> exportedClassnames = pluginResponse.getPluginDescriptor().getExportedClassnames();

            for ( ComponentDescriptor<?> componentDescriptor : this.plexusContainer.getComponentDescriptorList( PlexusResource.class.getName() ) )
            {
                if ( exportedClassnames.contains( componentDescriptor.getImplementation() ) )
                {
                    try
                    {
                        PlexusResource resource =
                            plexusContainer.lookup( PlexusResource.class, componentDescriptor.getRoleHint() );

                        RestInfo restInfo = new RestInfo();
                        restInfo.setUri( resource.getResourceUri() );

                        result.addRestInfo( restInfo );
                    }
                    catch ( ComponentLookupException e )
                    {
                        getLogger().warn(
                            "Unable to find PlexusResource '" + componentDescriptor.getImplementation() + "'.", e );
                    }
                }
            }
        }

        return result;
    }
}
