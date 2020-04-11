function applicationStarted(pluginWorkspaceAccess) {
  var nullFn = function() {};
  // Filter that blocks all the editing operations on the server.
  var blockAllEditsFilter = new JavaAdapter(Packages.ro.sync.ecss.extensions.api.AuthorDocumentFilter, {
    insertFragment: nullFn,
    insertMultipleElements: nullFn,
    insertMultipleFragments: nullFn,
    "delete": nullFn,
    deleteNode: nullFn,
    multipleDelete: nullFn,
    renameElement: nullFn,
    setAttribute: nullFn,
    removeAttribute: nullFn,
    split: nullFn,
    surroundWithNode: nullFn,
    surroundInFragment: nullFn,
    surroundInText: nullFn,
    setDoctype: nullFn,
    setMultipleDistinctAttributes: nullFn,
    setMultipleAttributes: nullFn,
    insertNode: nullFn,
    insertText: nullFn
  });
  pluginWorkspaceAccess.addEditingSessionLifecycleListener(
      new JavaAdapter(Packages.ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener, {
        editingSessionStarted: function (docId, authorDocumentModel) {
          authorDocumentModel.getAuthorAccess().getDocumentController().setDocumentFilter(blockAllEditsFilter);
        }
      }));
}

function applicationClosing(pluginWorkspaceAccess) {
  // Nothing to do.
}