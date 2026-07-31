package com.oxygenxml;

import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {

  private WebappEditingSessionLifecycleListener editingSessionLifecycleListener;

  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace webappPluginWorkspace = (WebappPluginWorkspace) pluginWorkspaceAccess;

    editingSessionLifecycleListener = new WebappEditingSessionLifecycleListener() {
      @Override
      public void editingSessionStarted(String editingSessionId, AuthorDocumentModel documentModel) {
        attachFilter(documentModel);
      }

      @Override
      public void editingSessionDeserialized(String editingSessionId, AuthorDocumentModel documentModel) {
        // The document model (and its underlying WebappValidationAssistant) is rebuilt when the
        // editing session is restored from disk. Any filter added on the previous instance is
        // gone, so it must be re-attached here as well, not just in editingSessionStarted.
        attachFilter(documentModel);
      }
    };

    webappPluginWorkspace.addEditingSessionLifecycleListener(editingSessionLifecycleListener);
  }

  private void attachFilter(AuthorDocumentModel documentModel) {
    documentModel.getWSEditor().addValidationProblemsFilter(
        new BrokenLinksValidationProblemsFilter(documentModel.getWSEditor()));
  }

  @Override
  public boolean applicationClosing() {
    return true;
  }

}
