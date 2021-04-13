function applicationStarted(pluginWorkspaceAccess) {
  var fixNewLines = new JavaAdapter(Packages.ro.sync.ecss.extensions.api.AuthorDocumentFilter, {
    addCommentMarker: function(filterBypass, startOffset, endOffset, comment, parentID) {
        filterBypass.addCommentMarker(startOffset, endOffset, comment.replaceAll("\r\n", "\n"), parentID);
    }
  });
  pluginWorkspaceAccess.addEditingSessionLifecycleListener(
      new JavaAdapter(Packages.ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener, {
        editingSessionStarted: function (docId, authorDocumentModel) {
          authorDocumentModel.getAuthorAccess().getDocumentController().setDocumentFilter(fixNewLines);
        }
      }));
}

function applicationClosing(pluginWorkspaceAccess) {
  // Nothing to do.
}
