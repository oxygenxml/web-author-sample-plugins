package com.oxygenxml.filter.attribute;

import ro.sync.ecss.extensions.api.attributes.AuthorAttributesDisplayFilter;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Plugin extension used to filter the "id" attributes
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {
  
  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace workspace = (WebappPluginWorkspace) pluginWorkspaceAccess;
    
    workspace.addEditingSessionLifecycleListener(new WebappEditingSessionLifecycleListener() {
      
      @Override
      public void editingSessionStarted(String sessionId, AuthorDocumentModel documentModel) {
    	  documentModel.getAuthorAccess().getEditorAccess().addAuthorAttributesDisplayFilter(new AuthorAttributesDisplayFilter() {
    		  @Override
    		public boolean shouldFilterAttribute(AuthorElement parentElement, String attributeQName, int source) {
    			return "id".equals(attributeQName);
    		}
		});
      }
    });
  }
  
  @Override
  public boolean applicationClosing() {
    return true;
  }

}
