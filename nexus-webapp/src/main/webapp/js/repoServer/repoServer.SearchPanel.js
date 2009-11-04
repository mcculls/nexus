/*
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
Sonatype.repoServer.SearchPanel = function(config){
  var config = config || {};
  var defaultConfig = {};
  Ext.apply(this, config, defaultConfig);
  
  this.searchTypes = [];
  
  //fire event for plugins to add their own search items
  Sonatype.Events.fireEvent( 'searchTypeInit', this.searchTypes, this );
  
  //no items, no page
  if ( this.searchTypes.length < 1 ) {
    return;
  }
  
  this.searchTypeButton = new Ext.Button({
    text: this.searchTypes[0].text,
    value: this.searchTypes[0].value,
    tooltip: 'Click for more search options',
    handler: this.switchSearchType,
    scope: this,
    menu: {
      items: this.searchTypes
    }
  });
  
  this.searchToolbar = new Ext.Toolbar({
    ctCls:'search-all-tbar',
    items: [
      this.searchTypeButton,
      this.convertToFieldObject( this.searchTypes[0].panelItems[0] )
    ]
  });
  
  this.grid = new Sonatype.repoServer.SearchResultGrid({
    searchPanel: this
  });
  
  this.artifactContainer = new Sonatype.repoServer.ArtifactContainer({
  });
  
  this.appletPanel = new Ext.Panel({
    fieldLabel: '',
    html: '<div style="width:10px"></div>'
  });
  
  this.filenameLabel = null;
  
  Sonatype.repoServer.SearchPanel.superclass.constructor.call(this, {
    layout: 'border',
    hideMode: 'offsets',
    tbar: this.searchToolbar,
    items: [
      this.grid,
      this.artifactContainer
    ]
  });
};

Ext.extend(Sonatype.repoServer.SearchPanel, Ext.Panel, {
  switchSearchType: function( button, event ) {
    if ( event == null
        || this.searchTypeButton.value != button.value ) {
      this.searchTypeButton.value = button.value;
      this.searchTypeButton.setText( this.getSearchType( button.value ).text );
      this.clearWarningLabel();
      this.loadSearchPanel();
    }
  },
  loadSearchPanel: function() {
    //first remove current items
    while ( this.searchToolbar.items.length > 1 ) {
      var item = this.searchToolbar.items.last();
      this.searchToolbar.items.remove( item );
      item.destroy();
    }
    
    //now add the other items
    var searchType = this.getSearchType( this.searchTypeButton.value );
    
    if ( searchType != null ) {
      for ( var i = 0; i < searchType.panelItems.length; i++ ) {
        //can't simply add object config to toolbar, need to create
        //a real item
        this.searchToolbar.add(
          this.convertToFieldObject( searchType.panelItems[i] ) 
        );
      }
    }
  },  
  convertToFieldObject : function( config ) {
    if ( config.xtype == 'nexussearchfield' ) {
      return new Ext.app.SearchField( config );
    }
    else if ( config.xtype == 'textfield' ) {
      return new Ext.form.TextField( config );
    }
    else {
      return config;
    }
  },  
  getSearchType : function( value ) {
    for ( var i = 0 ; i < this.searchTypes.length ; i++ ) {
      if ( this.searchTypes[i].value == value ) {
        return this.searchTypes[i];
      }
    }
    
    return null;
  },
  setWarningLabel : function( s ) {
    this.clearWarningLabel();
    this.warningLabel = this.searchToolbar.addText( '<span class="x-toolbar-warning">' + s + '</span>' );
  },
  clearWarningLabel : function() {
    if ( this.warningLabel ) {
      this.warningLabel.destroy();
      this.warningLabel = null;
    }
  },
  startSearch : function( panel ) {
    Sonatype.utils.updateHistory( panel );
    
    var searchType = this.getSearchType( this.searchTypeButton.value );
    
    searchType.searchHandler.call( this, panel );
  },
  setFilenameLabel : function( panel, s ) {
    if ( panel.filenameLabel ) {
      panel.filenameLabel.destroy();
    }
    panel.filenameLabel = s ? panel.searchToolbar.addText( '<span style="color:#808080;">'+s+'</span>' ) : null;
  },
  fetchRecords : function( panel ) {
    panel.artifactContainer.collapsePanel();
    panel.grid.totalRecords = 0;
    panel.grid.store.load();
  },
  startQuickSearch: function( v ) {
    var searchType = 'quick';
    if ( v.search(/^[0-9a-f]{40}$/) == 0 ) {
      searchType = 'checksum';
    }
    else if ( v.search(/^[a-z.]*[A-Z]/) == 0 ) {
      searchType = 'classname';
    }
    this.switchSearchType(
      {
        value: searchType
      },
      null
    );
    this.getTopToolbar().items.itemAt(1).setRawValue( v );
    this.startSearch( this );
  },
  applyBookmark : function( bookmark ) {
    if ( bookmark ) {
      var parts = bookmark.split( '~' );
      
      if ( parts.length == 1 ) {
        this.startQuickSearch( bookmark );
      }
      else if ( parts.length > 1 ) {
        this.switchSearchType(
          {
            value: parts[0]
          },
          null
        );
        
        var searchType = this.getSearchType( parts[0] );
        
        searchType.applyBookmarkHandler.call( this, this, parts );
      }
    }
  },
  getBookmark : function() {
    var searchType = this.getSearchType( this.searchTypeButton.value );
    
    return searchType.getBookmarkHandler.call( this, this );
  },
});

//Add the quick search
Sonatype.Events.addListener( 'searchTypeInit', function( searchTypes, panel ) {
  searchTypes.push({
    value: 'quick',
    text: 'Keyword Search',    
    scope: panel,
    handler: panel.switchSearchType,
    searchHandler: function( panel ) {
      var value = panel.getTopToolbar().items.itemAt(1).getRawValue();
      
      if ( value ) {
        panel.grid.store.baseParams = {};
        panel.grid.store.baseParams['q'] = value;
        panel.fetchRecords( panel );
      }
    },
    applyBookmarkHandler: function( panel, data ) {
      panel.getTopToolbar().items.itemAt(1).setRawValue( data[1] );
      panel.startSearch( panel );
    },
    getBookmarkHandler: function( panel ) {
      var result = panel.searchTypeButton.value;
      result += '~';
      result += panel.getTopToolbar().items.itemAt(1).getRawValue();
      
      return result;
    },
    panelItems: [
      {
        xtype: 'nexussearchfield',
        name: 'single-search-field',
        searchPanel: panel,
        width: 300
      }
    ]
  });
});

//Add the classname search
Sonatype.Events.addListener( 'searchTypeInit', function( searchTypes, panel ) {
  searchTypes.push({
    value: 'classname',
    text: 'Classname Search',
    scope: panel,
    handler: panel.switchSearchType,
    searchHandler: function( panel ) {
      var value = panel.getTopToolbar().items.itemAt(1).getRawValue();
      
      if ( value ) {
        panel.grid.store.baseParams = {};
        panel.grid.store.baseParams['cn'] = value;
        panel.fetchRecords( panel );
      }
    },
    applyBookmarkHandler: function( panel, data ) {
      panel.getTopToolbar().items.itemAt(1).setRawValue( data[1] );
      panel.startSearch( panel );
    },
    getBookmarkHandler: function( panel ) {
      var result = panel.searchTypeButton.value;
      result += '~';
      result += panel.getTopToolbar().items.itemAt(1).getRawValue();
      
      return result;
    },
    panelItems: [
      {
        xtype: 'nexussearchfield',
        name: 'single-search-field',
        searchPanel: panel,
        width: 300
      }
    ]
  });
});

//Add the gav search
Sonatype.Events.addListener( 'searchTypeInit', function( searchTypes, panel ) {
  var enterHandler = function(f, e) {
    if(e.getKey() == e.ENTER){
      this.startSearch( this );
    }
  };
  
  searchTypes.push({
    value: 'gav',
    text: 'GAV Search',
    scope: panel,
    handler: panel.switchSearchType,
    searchHandler: function( panel ) {
      this.grid.store.baseParams = {};
      
      //groupId
      var v = panel.getTopToolbar().items.itemAt(2).getRawValue();
      if ( v ) {
        panel.grid.store.baseParams['g'] = v;
      }
      //artifactId
      v = panel.getTopToolbar().items.itemAt(5).getRawValue();
      if ( v ) {
        panel.grid.store.baseParams['a'] = v;
      }
      //version
      v = panel.getTopToolbar().items.itemAt(8).getRawValue();
      if ( v ) {
        panel.grid.store.baseParams['v'] = v;
      }
      //packaging
      v = panel.getTopToolbar().items.itemAt(11).getRawValue();
      if ( v ) {
        panel.grid.store.baseParams['p'] = v;
      }
      //classifier
      v = panel.getTopToolbar().items.itemAt(14).getRawValue();
      if ( v ) {
        panel.grid.store.baseParams['c'] = v;
      }
      
      if ( panel.grid.store.baseParams['g'] == null 
          && panel.grid.store.baseParams['a'] == null 
          && panel.grid.store.baseParams['v'] == null ) {
        panel.setWarningLabel( 'A group, an artifact or a version is required to run a search.' );
        return;
      }
      
      panel.clearWarningLabel();
   
      panel.fetchRecords( panel );
    },
    applyBookmarkHandler: function( panel, data ) {
      //groupId
      panel.getTopToolbar().items.itemAt(2).setRawValue( data[1] );
      //artifactId
      panel.getTopToolbar().items.itemAt(5).setRawValue( data[2] );
      //version
      panel.getTopToolbar().items.itemAt(8).setRawValue( data[3] );
      //packaging
      panel.getTopToolbar().items.itemAt(11).setRawValue( data[4] );
      //classifier
      panel.getTopToolbar().items.itemAt(14).setRawValue( data[5] );
      panel.startSearch( this );
    },
    getBookmarkHandler: function( panel ) {
      var result = panel.searchTypeButton.value;
      //groupId
      result += '~';
      var v = panel.getTopToolbar().items.itemAt(2).getRawValue();
      if ( v ) {
        result += v;
      }
      //artifactId
      result += '~';
      v = panel.getTopToolbar().items.itemAt(5).getRawValue();
      if ( v ) {
        result += v;
      }
      //version
      result += '~';
      v = panel.getTopToolbar().items.itemAt(8).getRawValue();
      if ( v ) {
        result += v;
      }
      //packaging
      result += '~';
      v = panel.getTopToolbar().items.itemAt(11).getRawValue();
      if ( v ) {
        result += v;
      }
      //classifier
      result += '~';
      v = panel.getTopToolbar().items.itemAt(14).getRawValue();
      if ( v ) {
        result += v;
      }
      return result;
    },
    panelItems: [
      'Group:',
      { 
        xtype: 'textfield',
        id: 'gavsearch-group',
        size: 80,
        listeners: {          
        'specialkey': {
            fn: enterHandler,
            scope: panel
          }
        }
      },
      { xtype: 'tbspacer' },
      'Artifact:',
      { 
        xtype: 'textfield',
        id: 'gavsearch-artifact',
        size: 80,
        listeners: {
          'specialkey': {
            fn: enterHandler,
            scope: panel
          }
        }
      },
      { xtype: 'tbspacer' },
      'Version:',
      { 
        xtype: 'textfield',
        id: 'gavsearch-version',
        size: 80,
        listeners: {
          'specialkey': {
            fn: enterHandler,
            scope: panel
          }
        }
      },
      { xtype: 'tbspacer' },
      'Packaging:',
      { 
        xtype: 'textfield',
        id: 'gavsearch-packaging',
        size: 80,
        listeners: {
          'specialkey': {
            fn: enterHandler,
            scope: panel
          }
        }
      },
      { xtype: 'tbspacer' },
      'Classifier:',
      { 
        xtype: 'textfield',
        id: 'gavsearch-classifier',
        size: 80,
        listeners: {
          'specialkey': {
            fn: enterHandler,
            scope: panel
          }
        }
      },
      { xtype: 'tbspacer' },
      {
        icon: Sonatype.config.resourcePath + '/images/icons/search.gif',
        cls: 'x-btn-icon',
        scope: panel,
        handler: panel.startGAVSearch
      }
    ]
  });
});

//Add the checksum search
Sonatype.Events.addListener( 'searchTypeInit', function( searchTypes, panel ) {
  if ( Sonatype.lib.Permissions.checkPermission( 'nexus:identify', Sonatype.lib.Permissions.READ ) ) {
    searchTypes.push({
      value: 'checksum',
      text: 'Checksum Search',
      scope: panel,
      handler: panel.switchSearchType,
      searchHandler: function( panel ) {
        var value = panel.getTopToolbar().items.itemAt(1).getRawValue();
        
        if ( value ) {
          panel.grid.store.baseParams = {};
          panel.grid.store.baseParams['sha1'] = value;
          panel.fetchRecords( panel );
        }
      },
      applyBookmarkHandler: function( panel, data ) {
        panel.getTopToolbar().items.itemAt(1).setRawValue( data[1] );
        panel.startSearch( panel );
      },
      getBookmarkHandler: function( panel ) {
        var result = panel.searchTypeButton.value;
        result += '~';
        result += panel.getTopToolbar().items.itemAt(1).getRawValue();
        
        return result;
      },
      panelItems: [
        {
          xtype: 'nexussearchfield',
          name: 'single-search-field',
          searchPanel: panel,
          width: 300
        },
        {
          xtype: Ext.isGecko3 ? 'button' : 'browsebutton',
          text: 'Browse...',
          searchPanel: panel,
          tooltip: 'Click to select a file. It will not be uploaded to the ' +
            'remote server, an SHA1 checksum is calculated locally and sent to ' +
            'Nexus to find a match. This feature requires Java applet ' +
            'support in your web browser.',
          handler: function( b ) {
            if ( ! document.digestApplet ) {
              b.searchPanel.grid.fetchMoreBar.addText(
                '<div id="checksumContainer" style="width:10px">' +
                  '<applet code="org/sonatype/nexus/applet/DigestApplet.class" ' +
                    'archive="' + Sonatype.config.resourcePath + '/digestapplet.jar" ' +
                    'width="1" height="1" name="digestApplet"></applet>' +
                  '</div>'
              ); 
            }
        
            var filename = null;
            
            if ( Ext.isGecko3 ) {
              filename = document.digestApplet.selectFile();
            }
            else {
              var fileInput = b.detachInputFile();
              filename = fileInput.getValue();
            }
        
            if ( ! filename ) {
              return;
            }
            
            b.disable();
            b.searchPanel.setFilenameLabel( b.searchPanel, 'Calculating checksum...' );
        
            var f = function( b, filename ) {
              var sha1 = 'error calculating checksum';
              if ( document.digestApplet ) {
                sha1 = document.digestApplet.digest( filename );
              }
                
              b.searchPanel.getTopToolbar().items.itemAt(1).setRawValue( sha1 );
              b.searchPanel.setFilenameLabel( b.searchPanel, filename );
              b.enable();
              b.searchPanel.startSearch( b.searchPanel );
            }
            f.defer( 200, b, [b, filename] );
          }
        }
      ]
    });
  }
});
