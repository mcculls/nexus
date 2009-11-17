/**
 * Sonatype Nexus (TM) Professional Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions/.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.restlight.stage.it.utils;

import org.sonatype.plexus.rest.xstream.AliasingListConverter;

import com.sonatype.nexus.staging.api.dto.StagingProfileDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileListResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileRepositoriesListResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileRepositoryDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileRequestDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleDTO;
import com.sonatype.nexus.staging.api.dto.StagingRulePropertyDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetListResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetRequestDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleTypeDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleTypeListResponseDTO;
import com.thoughtworks.xstream.XStream;

public class XStreamFactory
{

    public static XStream getXmlXStream()
    {
        XStream xs = org.sonatype.nexus.test.utils.XStreamFactory.getXmlXStream();
        configureXStream( xs );
        return xs ;
    }

    public static XStream getJsonXStream()
    {
        XStream xs = org.sonatype.nexus.test.utils.XStreamFactory.getJsonXStream();
        configureXStream( xs );
        return xs ;
    }

    private static void configureXStream( XStream xstream )
    {
        xstream.processAnnotations( StagingProfileListResponseDTO.class );
        xstream.processAnnotations( StagingProfileDTO.class );
        xstream.processAnnotations( StagingProfileRequestDTO.class );
        xstream.processAnnotations( StagingProfileResponseDTO.class );
        xstream.processAnnotations( StagingProfileRepositoriesListResponseDTO.class );
        xstream.processAnnotations( StagingProfileRepositoryDTO.class );

        xstream.registerLocalConverter( StagingProfileListResponseDTO.class, "data", new AliasingListConverter(
            StagingProfileDTO.class,
            "stagingProfile" ) );

        xstream.registerLocalConverter( StagingProfileDTO.class, "currentRepositoryIds", new AliasingListConverter(
            String.class,
            "currentRepositoryId" ) );

        xstream.registerLocalConverter( StagingProfileDTO.class, "targetGroups", new AliasingListConverter(
            String.class,
            "targetGroup" ) );

        xstream.registerLocalConverter( StagingProfileDTO.class, "notifyUsers", new AliasingListConverter(
            String.class,
            "notifyUser" ) );

        xstream.registerLocalConverter( StagingProfileRepositoriesListResponseDTO.class, "stagingRepositories", new AliasingListConverter(
            StagingProfileRepositoryDTO.class,
            "stagingRepository" ) );
        
        xstream.processAnnotations( StagingRuleTypeDTO.class );
        xstream.processAnnotations( StagingRuleTypeListResponseDTO.class );
        xstream.registerLocalConverter( StagingRuleTypeListResponseDTO.class, "data", new AliasingListConverter(
            StagingRuleTypeDTO.class,
            "stagingRuleType" ) );
        
        
        xstream.processAnnotations( StagingRulePropertyDTO.class );
        xstream.processAnnotations( StagingRuleDTO.class );
        xstream.processAnnotations( StagingRuleSetDTO.class );
        xstream.processAnnotations( StagingRuleSetListResponseDTO.class );
        xstream.processAnnotations( StagingRuleSetRequestDTO.class );
        xstream.processAnnotations( StagingRuleSetResponseDTO.class );
        xstream.registerLocalConverter( StagingRuleDTO.class, "properties", new AliasingListConverter(
            StagingRulePropertyDTO.class,
            "property" ) );
        xstream.registerLocalConverter( StagingRuleSetDTO.class, "rules", new AliasingListConverter(
            StagingRuleDTO.class,
            "rule" ) );
        xstream.registerLocalConverter( StagingRuleSetListResponseDTO.class, "data", new AliasingListConverter(
            StagingRuleSetDTO.class,
            "ruleSet" ) );
    }

}
