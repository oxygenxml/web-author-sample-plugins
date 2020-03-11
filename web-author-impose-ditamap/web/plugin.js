(function() {
  let ditaContext = workspace.getEditingContextManager().getDitaContext();
  if (!ditaContext.ditamapUrl) {
    // The OXY-URL of the currently edited file. More details about how they are formed can be found here:
    //     https://www.oxygenxml.com/doc/ug-waCustom/topics/oxy-url.html
    let url = new URLSearchParams(window.location.search).get('url');

    // We assume a layout in which:
    //  - the DITA Map is called "UserManual.ditamap"
    //  - all topics are located under a topics folder
    ditaContext.ditamapUrl = url.replace(new RegExp('/topics/.*'), '/UserManual.ditamap');
  } else {
    // The DITA Map is already configured using the `ditamap` URL parameter.
  }
}());
