function applicationStarted(pluginWorkspaceAccess) {
  pluginWorkspaceAccess.addEditingSessionLifecycleListener(
      new JavaAdapter(Packages.ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener, {
        editingSessionStarted: function (docId, authorDocumentModel) {
          authorDocumentModel.getWSEditor().addEditorListener(new JavaAdapter(Packages.ro.sync.exml.workspace.api.listeners.WSEditorListener, {
            editorAboutToBeSavedVeto: function() {
              if (hasErrors(authorDocumentModel)) {
                var workspaceAccess = authorDocumentModel.getAuthorAccess().getWorkspaceAccess();
                workspaceAccess.showErrorMessage("Document is not valid");
                return false;
              }
              return true;
            }
          }))
        }
      }));
}

/**
 * Get the errors of a specific document.
 * @param {string} authorDocumentModel The AuthorDocumentModel instance.
 *
 * @return {boolean} true if the document has validation errors.
 */
function hasErrors(authorDocumentModel) {
  var errors = authorDocumentModel.getDocumentValidator().getValidationTask().call();
  return errors && !errors.isEmpty();
}

function applicationClosing(pluginWorkspaceAccess) {
  // Nothing to do.
}
