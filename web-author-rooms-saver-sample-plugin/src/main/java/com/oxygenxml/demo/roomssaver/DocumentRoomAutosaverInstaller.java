package com.oxygenxml.demo.roomssaver;

import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Entrypoint extension point that saves the concurrently edited document after each edit.</br>
 * It adds the <code>EditListenerInstaller</code>, an <i>editing session lifecycle listener</i>
 * that will add an {@link ro.sync.ecss.extensions.api.webapp.ce.RoomObserver.EditListener}
 * for each {@link ro.sync.ecss.extensions.api.webapp.ce.Room}.</br>
 */
public class DocumentRoomAutosaverInstaller implements WorkspaceAccessPluginExtension {

  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace pluginWorkspace =
        (WebappPluginWorkspace)PluginWorkspaceProvider.getPluginWorkspace();

    pluginWorkspace.addEditingSessionLifecycleListener(
        new EditListenerInstaller());
  }
  @Override
  public boolean applicationClosing() {
    return true;
  }
}
