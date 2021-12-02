package com.oxygenxml;

import java.net.URL;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {

  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WSEditorChangeListener mainEditorChangedListener = new WSEditorChangeListener() {
      @Override
      public void editorOpened(URL editorLocation) {
        /*Get the opened editor*/
        WSEditor editor = pluginWorkspaceAccess.getEditorAccess(editorLocation, PluginWorkspace.MAIN_EDITING_AREA);
        /*Add validation problems filter*/
        editor.addValidationProblemsFilter(
            new CustomValidationProblemsFilter());

      }
      
    };
    
    pluginWorkspaceAccess.addEditorChangeListener(mainEditorChangedListener, PluginWorkspace.MAIN_EDITING_AREA);
    
  }

  @Override
  public boolean applicationClosing() {
    return true;
  }

}
