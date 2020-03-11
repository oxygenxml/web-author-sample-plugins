(function() {
  var config = window.ditaMapConfig;
  let ditaContext = workspace.getEditingContextManager().getDitaContext();
  if (config && !ditaContext.ditamapUrl) {
    let singleMapQueryString = config.singleDITAMap && config.singleDITAMap.queryString;
    if (singleMapQueryString) {
      ditaContext.ditamapUrl = new URLSearchParams(singleMapQueryString).get('url');
    } else {
      var ditaMapFileName = config.singleTopicLayout && config.singleTopicLayout.ditaMapFileName;
      if (ditaMapFileName) {
        let url = new URLSearchParams(window.location.search).get('url');
        ditaContext.ditamapUrl = url.replace(new RegExp('/topics/.*'), '/' + ditaMapFileName);
      }
    }
  } else {
    // The DITA Map is already configured using the `ditamap` URL parameter.
  }
}());
