package org.sonatype.nexus.integrationtests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.sonatype.nexus.integrationtests.client.nexus758.Nexus758StatusService;
import org.sonatype.nexus.integrationtests.nexus166.Nexus166SampleTest;
import org.sonatype.nexus.integrationtests.nexus167.Nexus167ReleaseToSnapshotTest;
import org.sonatype.nexus.integrationtests.nexus169.Nexus169ReleaseMetaDataInSnapshotRepoTest;
import org.sonatype.nexus.integrationtests.nexus258.Nexus258ReleaseDeployTest;
import org.sonatype.nexus.integrationtests.webproxy.nexus1116.Nexus1116InvalidProxyTest;

/**
 *
 */
@RunWith( Suite.class )
@SuiteClasses( {
    Nexus166SampleTest.class,
    Nexus758StatusService.class,
    Nexus169ReleaseMetaDataInSnapshotRepoTest.class,
    Nexus258ReleaseDeployTest.class,
    Nexus167ReleaseToSnapshotTest.class,
//    Nexus168SnapshotToReleaseTest.class,
//    Nexus176DeployToInvalidRepoTest.class,
//    Nexus259SnapshotDeployTest.class,
//    Nexus260MultipleDeployTest.class,
//    Nexus261NexusGroupDownloadTest.class,
//    Nexus177OutOfServiceTest.class,
//    Nexus178BlockProxyDownloadTest.class,
//    Nexus179RemoteRepoDownTest.class,
//    Nexus262SimpleProxyTest.class,
//    Nexus292SoftRestartTest.class,
//    Nexus133TargetCrudJsonTests.class,
//    Nexus133TargetCrudXmlTests.class,
//    Nexus142UserCrudJsonTests.class,
//    Nexus142UserCrudXmlTests.class,
//    Nexus156RolesCrudJsonTests.class,
//    Nexus156RolesCrudXmlTests.class,
//    Nexus142UserValidationTests.class,
//    Nexus156RolesValidationTests.class,
//    Nexus133TargetValidationTests.class,
//    Nexus233PrivilegesCrudXMLTests.class,
//    Nexus233PrivilegesValidationTests.class,
//    Nexus393ResetPasswordTest.class,
//    Nexus394ForgotPasswordTest.class,
//    Nexus385RoutesCrudXmlTests.class,
//    Nexus385RoutesValidationTests.class,
//    Nexus387RoutesTests.class,
//    Nexus395ForgotUsernameTest.class,
//    Nexus408ChangePasswordTest.class,
//    Nexus526FeedsTests.class,
//    Nexus531RepositoryCrudXMLTests.class,
//    Nexus531RepositoryCrudJsonTests.class,
//    Nexus533TaskManualTest.class,
//    Nexus533TaskOnceTest.class,
//    Nexus533TaskWeeklyTest.class,
//    Nexus533TaskMonthlyTest.class,
//    Nexus533TaskCronTest.class,
//    Nexus533TaskCronTest.class,
//    Nexus233PrivilegesCrudXMLTests.class,
//    Nexus379VirtualRepoSameId.class,
//    Nexus448PrivilegeURLTest.class,
//    Nexus586AnonymousChangePasswordTest.class,
//    Nexus586AnonymousForgotPasswordTest.class,
//    Nexus586AnonymousForgotUserIdTest.class,
//    Nexus586AnonymousResetPasswordTest.class,
//    Nexus532GroupsCrudXmlTests.class,
//    Nexus606DownloadLogsAndConfigFilesTest.class,
//    Nexus643EmptyTrashTaskTest.class,
//    Nexus637PublishIndexTest.class,
//    Nexus652Beta5To10UpgradeTest.class,
//    Nexus602SearchSnapshotArtifactTest.class,
//    Nexus635ClearCacheTaskTest.class,
//    Nexus634RemoveAllTest.class,
//    Nexus634KeepNewSnapshotsTest.class,
//    Nexus634KeepTwoSnapshotsTest.class,
//    Nexus636EvictUnusedProxiedTaskTest.class,
//    Nexus639PurgeTaskTest.class,
//    Nexus598ClassnameSearchTest.class,
//    Nexus640RebuildRepositoryAttributesTaskTest.class,
//    Nexus531RepositoryCrudValidationTests.class,
//    Nexus810PackageNamesInRestMessages.class,
//    Nexus810PackageNamesInNexusConf.class,
//    Nexus782UploadWithClassifier.class,
//    Nexus688ReindexOnRepoAdd.class,
//    Nexus384DotAndDashSearchTest.class,
//    Nexus642SynchShadowTaskTest.class,
//    Nexus947GroupBrowsing.class,
//    Nexus570IndexArchetypeTest.class,
//    Nexus970DeleteRepositoryTest.class,
//    Nexus969CacheEvictInteractionTest.class,
//    Nexus980ReindexVirtualReposTest.class,
//    Nexus950CorruptPomTest.class,
//    Nexus779DeployRssTest.class,
//    Nexus639PurgeTaskTest.class,
//    Nexus1022RebuildRepositoryMavenMetadataTaskTest.class,
//    Nexus1071DeployToRepoAnonCannotAccess.class,
//    Nexus1071AnonAccessTest.class,
//    Nexus1101NexusOverWebproxyTest.class,
//    Nexus1113WebProxyWithAuthenticationTest.class,
    Nexus1116InvalidProxyTest.class
} )
public class IntegrationTestSuiteClasses
{

}
