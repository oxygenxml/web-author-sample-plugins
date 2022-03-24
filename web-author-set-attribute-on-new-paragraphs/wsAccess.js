function applicationStarted(pluginWorkspaceAccess) {
  pluginWorkspaceAccess.addEditingSessionLifecycleListener(
      new JavaAdapter(Packages.ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener, {
        editingSessionStarted: function (docId, authorDocumentModel) {
          var adapter = new JavaAdapter(Packages.ro.sync.ecss.extensions.api.AuthorDocumentFilter, {
            insertFragment: function(bypass, offset, frag) {
              var contentNodes = frag.getContentNodes();
              var nodesCount = contentNodes.size();
              for (var i = 0; i <nodesCount; i++) {
                var node = contentNodes.get(i);
                if (node instanceof Packages.ro.sync.ecss.extensions.api.node.AuthorElement && "p".equals(node.getName())) {
                  node.setAttribute("outputclass", new Packages.ro.sync.ecss.extensions.api.node.AttrValue("normal"));
                }
              }
              return bypass.insertFragment(offset, frag);
            }
          });
          authorDocumentModel.getAuthorAccess().getDocumentController().setDocumentFilter(adapter);
        }
      }));
}

function applicationClosing(pluginWorkspaceAccess) {
  // Nothing to do.
}
