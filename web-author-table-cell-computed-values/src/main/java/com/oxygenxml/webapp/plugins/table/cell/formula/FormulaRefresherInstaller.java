package com.oxygenxml.webapp.plugins.table.cell.formula;

import ro.sync.ecss.extensions.api.AuthorListenerAdapter;
import ro.sync.ecss.extensions.api.DocumentContentDeletedEvent;
import ro.sync.ecss.extensions.api.DocumentContentInsertedEvent;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

import java.util.concurrent.atomic.AtomicBoolean;

public class FormulaRefresherInstaller implements WorkspaceAccessPluginExtension {

  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace webappPluginWorkspace = (WebappPluginWorkspace) pluginWorkspaceAccess;
    webappPluginWorkspace.addEditingSessionLifecycleListener(new WebappEditingSessionLifecycleListener() {
      @Override
      public void editingSessionStarted(String sessionId, AuthorDocumentModel documentModel) {
        FormulaRefresher refresher = new FormulaRefresher(documentModel.getAuthorAccess());
        AtomicBoolean isExecuting = new AtomicBoolean(false);
        documentModel.getAuthorDocumentController().addAuthorListener(new AuthorListenerAdapter() {
          private void refresh(AuthorNode node) {
            if (!isExecuting.getAndSet(true)) {
              refresher.refresh(node);
              isExecuting.set(false);
            }
          }
          @Override
          public void authorNodeStructureChanged(AuthorNode node) {
            refresh(node);
          }

          @Override
          public void contentDeleted(DocumentContentDeletedEvent e) {
            refresh(e.getParentNode());
          }

          @Override
          public void contentInserted(DocumentContentInsertedEvent e) {
            refresh(e.getParentNode());
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
