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
package org.sonatype.nexus.rest.repositories;

import java.io.IOException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.sonatype.configuration.ConfigurationException;
import org.sonatype.nexus.configuration.model.CLocalStorage;
import org.sonatype.nexus.configuration.model.CRemoteStorage;
import org.sonatype.nexus.configuration.model.CRepository;
import org.sonatype.nexus.configuration.model.DefaultCRepository;
import org.sonatype.nexus.proxy.maven.ChecksumPolicy;
import org.sonatype.nexus.proxy.maven.RepositoryPolicy;
import org.sonatype.nexus.proxy.maven.maven2.M2LayoutedM1ShadowRepositoryConfiguration;
import org.sonatype.nexus.proxy.maven.maven2.M2RepositoryConfiguration;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.LocalStatus;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.ShadowRepository;
import org.sonatype.nexus.rest.model.RepositoryBaseResource;
import org.sonatype.nexus.rest.model.RepositoryProxyResource;
import org.sonatype.nexus.rest.model.RepositoryResource;
import org.sonatype.nexus.rest.model.RepositoryResourceRemoteStorage;
import org.sonatype.nexus.rest.model.RepositoryResourceResponse;
import org.sonatype.nexus.rest.model.RepositoryShadowResource;
import org.sonatype.nexus.rest.util.EnumUtil;
import org.sonatype.nexus.templates.NoSuchTemplateIdException;
import org.sonatype.nexus.templates.TemplateManager;
import org.sonatype.nexus.templates.repository.RepositoryTemplate;
import org.sonatype.plexus.rest.resource.PathProtectionDescriptor;
import org.sonatype.plexus.rest.resource.PlexusResource;
import org.sonatype.plexus.rest.resource.PlexusResourceException;
import org.sonatype.plexus.rest.resource.error.ErrorResponse;

/**
 * A resource list for Repository list.
 * 
 * @author cstamas
 */
@Component( role = PlexusResource.class, hint = "RepositoryListPlexusResource" )
public class RepositoryListPlexusResource
    extends AbstractRepositoryPlexusResource
{

    @Requirement
    private TemplateManager templateManager;

    public RepositoryListPlexusResource()
    {
        this.setModifiable( true );
    }

    @Override
    public Object getPayloadInstance()
    {
        return new RepositoryResourceResponse();
    }

    @Override
    public String getResourceUri()
    {
        return "/repositories";
    }

    @Override
    public PathProtectionDescriptor getResourceProtection()
    {
        return new PathProtectionDescriptor( getResourceUri(), "authcBasic,perms[nexus:repositories]" );
    }

    @Override
    public Object get( Context context, Request request, Response response, Variant variant )
        throws ResourceException
    {
        return listRepositories( request, false, false );
    }

    @Override
    public Object post( Context context, Request request, Response response, Object payload )
        throws ResourceException
    {
        RepositoryResourceResponse repoRequest = (RepositoryResourceResponse) payload;
        String repoId = null;

        if ( repoRequest != null )
        {
            RepositoryBaseResource resource = repoRequest.getData();
            repoId = resource.getId();
            String providerHint = resource.getProvider();

            try
            {
                RepositoryTemplate template = (RepositoryTemplate) this.templateManager.getTemplates().getTemplateById( resource.getProvider() );

                // UGLY HACK
                // This is all broken here, the conversions that happens (Repo REST DTO -> CRepo DTO -> Repo creation)
                // is simply damn too stupid.
                // All this should be removed, and do not use C* config classes anymore in REST API (see NEXUS-2505).
                // For now, this is a "backdoor", using manual template when we have a CRepo object.
                CRepository config = template.getCoreConfiguration().getConfiguration( true );
                
                getRepositoryAppModel( resource, config );
                
                template.create();

                getNexusConfiguration().saveConfiguration();
            }
            catch ( ConfigurationException e )
            {
                handleConfigurationException( e );
            }
            catch ( IOException e )
            {
                getLogger().warn( "Got IO Exception!", e );

                throw new ResourceException( Status.SERVER_ERROR_INTERNAL );
            }
            catch ( NoSuchTemplateIdException e )
            {
                this.getLogger().debug(  "Could not find repository template with ID: "+ resource.getProvider() );
                ErrorResponse nexusErrorResponse = getNexusErrorResponse( "*", "Could not find repository template with ID: "+ resource.getProvider() );
                throw new PlexusResourceException( Status.CLIENT_ERROR_BAD_REQUEST, "Configuration error.", nexusErrorResponse );
            }
        }

        return getRepositoryResourceResponse( repoId );
    }

    // --

    /**
     * Converting REST DTO + possible App model to App model. If app model is given, "update" happens, otherwise if
     * target is null, "create".
     * 
     * @param model
     * @param target
     * @return app model, merged or created
     * @throws ResourceException
     */
    public CRepository getRepositoryAppModel( RepositoryBaseResource resource, CRepository target )
        throws ResourceException
    {
        if( target == null)
        {
            target = new DefaultCRepository();
            target.setLocalStatus( LocalStatus.IN_SERVICE.name() );
        }
        
        Xpp3Dom ex = (Xpp3Dom) target.getExternalConfiguration();;
        
        if( ex == null)
        {
            ex = new Xpp3Dom( DefaultCRepository.EXTERNAL_CONFIGURATION_NODE_NAME );
        }
        
        target.setId( resource.getId() );

        target.setName( resource.getName() );

        target.setExposed( resource.isExposed() );

        if ( REPO_TYPE_VIRTUAL.equals( resource.getRepoType() ) )
        {
            target.setProviderRole( ShadowRepository.class.getName() );

            target.setExternalConfiguration( ex );

            // indexer is unaware of the m2 layout conversion
            target.setIndexable( false );

            RepositoryShadowResource repoResource = (RepositoryShadowResource) resource;

            M2LayoutedM1ShadowRepositoryConfiguration exConf = new M2LayoutedM1ShadowRepositoryConfiguration( ex );

            exConf.setMasterRepositoryId( repoResource.getShadowOf() );

            exConf.setSynchronizeAtStartup( repoResource.isSyncAtStartup() );

        }
        else if ( REPO_TYPE_GROUP.equals( resource.getRepoType() ) )
        {
            target.setProviderRole( GroupRepository.class.getName() );
        }
        else
        {

            if( target.getProviderRole() == null )
            {  
                target.setProviderRole( Repository.class.getName() );
            }
            
            RepositoryResource repoResource = (RepositoryResource) resource;

            // we can use the default if the value is empty
            if ( StringUtils.isNotEmpty( repoResource.getWritePolicy() ) )
            {
                target.setWritePolicy( repoResource.getWritePolicy() );
            }

            target.setBrowseable( repoResource.isBrowseable() );

            target.setIndexable( repoResource.isIndexable() );
            target.setSearchable( repoResource.isIndexable() );

            target.setNotFoundCacheTTL( repoResource.getNotFoundCacheTTL() );

            target.setExternalConfiguration( ex );

            M2RepositoryConfiguration exConf = new M2RepositoryConfiguration( ex );

            exConf.setRepositoryPolicy( EnumUtil.valueOf( repoResource.getRepoPolicy(), RepositoryPolicy.class ) );

            if ( repoResource.getOverrideLocalStorageUrl() != null )
            {
                target.setLocalStorage( new CLocalStorage() );

                target.getLocalStorage().setUrl( repoResource.getOverrideLocalStorageUrl() );

                target.getLocalStorage().setProvider( "file" );
            }
            else
            {
                target.setLocalStorage( null );
            }

            RepositoryResourceRemoteStorage remoteStorage = repoResource.getRemoteStorage();
            if ( remoteStorage != null )
            {
                target.setRemoteStorage( new CRemoteStorage() );

                target.getRemoteStorage().setUrl( remoteStorage.getRemoteStorageUrl() );

                target.getRemoteStorage().setProvider( "apacheHttpClient3x" );
            }
        }

//        target.setProviderHint( resource.getProvider() );

        if ( RepositoryProxyResource.class.isAssignableFrom( resource.getClass() ) )
        {
            target = getRepositoryProxyAppModel( (RepositoryProxyResource) resource, target );
        }

        return target;
    }

    /**
     * Converting REST DTO + possible App model to App model. If app model is given, "update" happens, otherwise if
     * target is null, "create".
     * 
     * @param model
     * @param target
     * @return app model, merged or created
     * @throws PlexusResourceException
     */
    public CRepository getRepositoryProxyAppModel( RepositoryProxyResource model, CRepository target )
        throws PlexusResourceException
    {
        M2RepositoryConfiguration exConf = new M2RepositoryConfiguration( (Xpp3Dom) target.getExternalConfiguration() );

        exConf.setChecksumPolicy( EnumUtil.valueOf( model.getChecksumPolicy(), ChecksumPolicy.class ) );

        exConf.setDownloadRemoteIndex( model.isDownloadRemoteIndexes() );

        exConf.setArtifactMaxAge( model.getArtifactMaxAge() );

        exConf.setMetadataMaxAge( model.getMetadataMaxAge() );

        if ( model.getRemoteStorage() != null )
        {
            if ( target.getRemoteStorage() == null )
            {
                target.setRemoteStorage( new CRemoteStorage() );
            }

            // url
            target.getRemoteStorage().setUrl( model.getRemoteStorage().getRemoteStorageUrl() );

            // remote auth
            target.getRemoteStorage().setAuthentication(
                this.convertAuthentication( model.getRemoteStorage().getAuthentication(), null ) );

            // connection settings
            target.getRemoteStorage().setConnectionSettings(
                this.convertRemoteConnectionSettings( model.getRemoteStorage().getConnectionSettings() ) );

            // http proxy settings
            target.getRemoteStorage().setHttpProxySettings(
                this.convertHttpProxySettings( model.getRemoteStorage().getHttpProxySettings(), null ) );
        }

        return target;
    }

}
