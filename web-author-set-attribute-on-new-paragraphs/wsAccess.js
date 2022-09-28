function applicationStarted(pluginWorkspaceAccess) {
  pluginWorkspaceAccess.addEditingSessionLifecycleListener(
      new JavaAdapter(Packages.ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener, {
        editingSessionStarted: function (docId, authorDocumentModel) {
          var adapter = new JavaAdapter(Packages.ro.sync.ecss.extensions.api.AuthorDocumentFilter, {
            insertFragment: function(bypass, offset, frag) {
              frag.getContentNodes().forEach(node => setNormalOutputClassOnAllParas(node));
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

function setNormalOutputClassOnAllParas(parentNode) {
  if (parentNode instanceof Packages.ro.sync.ecss.extensions.api.node.AuthorElement) {
    setNormalOutputClassIfPara(parentNode);
    parentNode.getContentNodes().forEach(node => setNormalOutputClassOnAllParas(node));
  }
}

function setNormalOutputClassIfPara(element) {
  if ("p".equals(element.getName())) {
    element.setAttribute("outputclass", new Packages.ro.sync.ecss.extensions.api.node.AttrValue("normal"));
  }
}
