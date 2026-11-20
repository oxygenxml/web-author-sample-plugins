package com.oxygenxml.sessions;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.ecss.extensions.api.AttributeChangedEvent;
import ro.sync.ecss.extensions.api.AuthorListenerAdapter;
import ro.sync.ecss.extensions.api.DocumentContentDeletedEvent;
import ro.sync.ecss.extensions.api.DocumentContentInsertedEvent;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Listens for the editing sessions lifecycle and records who edits what in the
 * {@link EditingSessionsRegistry}.
 */
public class EditingSessionsTracker implements WorkspaceAccessPluginExtension {

  private static final Logger logger = LoggerFactory.getLogger(EditingSessionsTracker.class);

  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace workspace = (WebappPluginWorkspace) pluginWorkspaceAccess;
    // Documents opened in the editor.
    workspace.addEditingSessionLifecycleListener(new Listener("Document"));
    // DITA Maps opened in the DITA Maps Manager side-view.
    workspace.addDITAMapEditingSessionLifecycleListener(new Listener("DITA Map"));
  }

  @Override
  public boolean applicationClosing() {
    return true;
  }

  /**
   * The lifecycle listener.
   */
  private static class Listener extends WebappEditingSessionLifecycleListener {
    private final String kind;

    Listener(String kind) {
      this.kind = kind;
    }

    @Override
    public void editingSessionStarted(String editingSessionId, AuthorDocumentModel documentModel) {
      try {
        EditingSessionsRegistry.SessionInfo info = new EditingSessionsRegistry.SessionInfo(
            editingSessionId,
            kind,
            documentModel.getLicenseeId(),
            documentModel.getUserId(),
            // At this point Web Author did not yet set the author name (it does so right after
            // this event), so this is usually the default one. It is refreshed when displayed.
            getAuthorName(documentModel),
            getSystemId(documentModel));
        info.documentModel = documentModel;
        EditingSessionsRegistry.sessionStarted(info);
        installEditTracker(editingSessionId, documentModel);
      } catch (RuntimeException e) {
        // Never break the document loading because of this plugin.
        logger.warn("Could not record editing session " + editingSessionId, e);
      }
    }

    @Override
    public void editingSessionDeserialized(String editingSessionId, AuthorDocumentModel documentModel) {
      EditingSessionsRegistry.SessionInfo info = EditingSessionsRegistry.get(editingSessionId);
      if (info == null) {
        // E.g. the server (or this plugin) was restarted after the session was started.
        editingSessionStarted(editingSessionId, documentModel);
        return;
      }
      // The document model may be a new instance, refresh the details.
      try {
        info.authorName = getAuthorName(documentModel);
        info.userId = documentModel.getUserId();
        info.systemId = getSystemId(documentModel);
        // The document model is a new instance, created from the serialized session.
        installEditTracker(editingSessionId, documentModel);
      } catch (RuntimeException e) {
        logger.warn("Could not refresh editing session " + editingSessionId, e);
      }
      info.documentModel = documentModel;
      info.state = EditingSessionsRegistry.State.ACTIVE;
      info.lastActivity = System.currentTimeMillis();
    }

    @Override
    public void editingSessionAboutToBeSerialized(String editingSessionId, AuthorDocumentModel documentModel) {
      EditingSessionsRegistry.sessionSerialized(editingSessionId);
    }

    @Override
    public void editingSessionClosed(String editingSessionId, AuthorDocumentModel documentModel) {
      EditingSessionsRegistry.sessionClosed(editingSessionId);
    }
  }

  /**
   * Records the time of the last modification of the document content in the given session.
   * <p>
   * The listener only looks up the session by id, so it does not keep the registry entry alive.
   * It is attached to the document model, so it goes away together with it.
   */
  private static void installEditTracker(final String editingSessionId, AuthorDocumentModel documentModel) {
    documentModel.getAuthorDocumentController().addAuthorListener(new AuthorListenerAdapter() {
      @Override
      public void contentInserted(DocumentContentInsertedEvent e) {
        EditingSessionsRegistry.documentEdited(editingSessionId);
      }

      @Override
      public void contentDeleted(DocumentContentDeletedEvent e) {
        EditingSessionsRegistry.documentEdited(editingSessionId);
      }

      @Override
      public void attributeChanged(AttributeChangedEvent e) {
        EditingSessionsRegistry.documentEdited(editingSessionId);
      }

      @Override
      public void authorNodeNameChanged(AuthorNode node) {
        EditingSessionsRegistry.documentEdited(editingSessionId);
      }

      @Override
      public void authorNodeStructureChanged(AuthorNode node) {
        EditingSessionsRegistry.documentEdited(editingSessionId);
      }
    });
  }

  /**
   * @return The author name used for comments / track changes in the given session.
   */
  private static String getAuthorName(AuthorDocumentModel documentModel) {
    return documentModel.getReviewController().getReviewerAuthorName();
  }

  /**
   * @return The URL of the edited document.
   */
  private static String getSystemId(AuthorDocumentModel documentModel) {
    String systemId = documentModel.getAuthorDocumentController().getAuthorDocumentNode().getSystemID();
    return stripUserInfo(systemId);
  }

  /**
   * Removes the user-info (user:password@) part of an URL, if any, so it is not displayed.
   */
  private static String stripUserInfo(String systemId) {
    if (systemId == null) {
      return null;
    }
    try {
      URL url = new URL(systemId);
      if (url.getUserInfo() != null) {
        return systemId.replace(url.getUserInfo() + "@", "");
      }
    } catch (Exception e) {
      // Not an URL we can parse, keep it as it is.
    }
    return systemId;
  }
}
