function applicationStarted(pluginWorkspaceAccess) {
  var fixNewLines = new JavaAdapter(Packages.ro.sync.ecss.extensions.api.AuthorDocumentFilter, {
    addCommentMarker: function(filterBypass, startOffset, endOffset, comment, parentID) {
      var fixedComment = new Packages.java.lang.String(comment)
        .replaceAll("\r?\n", "\r\n")
        .replaceAll("\r\n?", "\r\n");
      filterBypass.addCommentMarker(startOffset, endOffset, fixedComment, parentID);
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
