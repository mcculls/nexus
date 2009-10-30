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
    region: 'west',
    split: true
  });
  
  this.artifactContainer = new Sonatype.repoServer.ArtifactContainer({
    collapsible: false,
    collapsed: false,
    region: 'center',
    artifactInformationLayout: 'vertical'
  });
  
  Sonatype.repoServer.RepositoryBrowsePanel.superclass.constructor.call( this, {
    layout: 'border',
    items: [
      this.repositoryBrowser,
      this.artifactContainer
    ]
  });
};

Ext.extend( Sonatype.repoServer.RepositoryBrowserContainer, Ext.Panel, {
  
});

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
