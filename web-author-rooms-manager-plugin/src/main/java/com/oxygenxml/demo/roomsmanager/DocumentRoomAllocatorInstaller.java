package com.oxygenxml.demo.roomsmanager;

import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Installs the DocumentRoomAllocator.
 */
public class DocumentRoomAllocatorInstaller implements WorkspaceAccessPluginExtension {

  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace pluginWorkspace =
        (WebappPluginWorkspace)PluginWorkspaceProvider.getPluginWorkspace();

    pluginWorkspace.addEditingSessionLifecycleListener(
        new DocumentRoomAllocator());
  }

  @Override
  public boolean applicationClosing() {
    return true;
  }
}
