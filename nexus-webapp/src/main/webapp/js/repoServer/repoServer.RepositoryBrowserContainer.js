// This container will host both the repository browser and the artifact information panel
Sonatype.repoServer.RepositoryBrowserContainer = function( config ) {
  var config = config || {};
  var defaultConfig = {
    browseIndex: false
  };
  Ext.apply( this, config, defaultConfig );
  
  this.repositoryBrowser = new Sonatype.repoServer.RepositoryBrowsePanel( { 
    payload: this.payload,
    tabTitle: this.tabTitle,
    browseIndex: this.browseIndex,
    region: 'center',
    nodeClickEvent: 'nodeClickedEvent',
    nodeClickPassthru: {
      container: this
    }
  });
  
  this.artifactContainer = new Sonatype.repoServer.ArtifactContainer({
    collapsible: true,
    collapsed: true,
    region: 'east',
    split: true,
    width: 500,
    artifactInformationLayout: 'vertical'
  });
  
  Sonatype.repoServer.RepositoryBrowserContainer.superclass.constructor.call( this, {
    layout: 'border',
    //this hideMode causes the tab to properly render when coming back from hidden
    hideMode: 'offsets',
    items: [
      this.repositoryBrowser,
      this.artifactContainer
    ]
  });
};

Ext.extend( Sonatype.repoServer.RepositoryBrowserContainer, Ext.Panel, {
  
});

// Add the browse storage and browse index panels to the repo
Sonatype.Events.addListener( 'repositoryViewInit', function( cardPanel, rec ) {
  if ( rec.data.resourceURI ) {
    cardPanel.add( new Sonatype.repoServer.RepositoryBrowserContainer( { 
      payload: rec,
      name: 'browsestorage',
      tabTitle: 'Browse Storage'
    } ) );
    if ( rec.data.repoType != 'virtual' 
      && rec.data.format == 'maven2' ) {
      cardPanel.add( new Sonatype.repoServer.RepositoryBrowserContainer( { 
        payload: rec,
        name: 'browseindex',
        tabTitle: 'Browse Index',
        browseIndex: true
      } ) );
    }
  }
} );

Sonatype.Events.addListener( 'nodeClickedEvent', function( node, passthru ) {
  if ( passthru 
      && passthru.container ) {
    if ( node 
        && node.isLeaf() ) {
      passthru.container.artifactContainer.updateArtifact({
        groupId: 'test-group',
        artifactId: 'test-artifact',
        version: '1.0-SNAPSHOT'
      });
    }
    else {
      passthru.container.artifactContainer.collapse();
    }
  }
});
