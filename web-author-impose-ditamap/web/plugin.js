(function() {
  let config = window.ditaMapConfig;
  let editingContextManager = workspace.getEditingContextManager();
  let ditaContext = editingContextManager.getDitaContext();
  if (config && !ditaContext.ditamapUrl) {
    let newDitaMapUrl = null;
    let singleMapQueryString = config.singleDITAMap && config.singleDITAMap.queryString;
    if (singleMapQueryString) {
      newDitaMapUrl = new URLSearchParams(singleMapQueryString).get('url');
    } else {
      let ditaMapFileName = config.singleTopicLayout && config.singleTopicLayout.ditaMapFileName;
      if (ditaMapFileName) {
        let url = new URLSearchParams(window.location.search).get('url');
        if (url) {
          newDitaMapUrl = url.replace(new RegExp('/topics/.*'), '/' + ditaMapFileName);
        }
      }
    }
    if (newDitaMapUrl !== null) {
      editingContextManager.updateDitaContext(new sync.api.DitaContext(newDitaMapUrl, ditaContext.filterUrl));
    }
  } else {
    // The DITA Map is already configured using the `ditamap` URL parameter.
  }
}());
