(function () {
  goog.events.listen(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function (e) {
    var editor = e.editor;
    var editingSupport = editor.getEditingSupport()
    editingSupport.getActionsManager().invokeOperation('com.oxygenxml.AddHighlightsOnParagraphsOperation');
  });
  
  loadCSS('../plugin-resources/custom-css/custom-style.css');
  
  /**
   * Dynamically load a CSS file in the document head.
   
   * @param {string} cssUrl The URL of the CSS to load.
   */
  function loadCSS(cssUrl) {
    var link = document.createElement('link');
    link.rel = "stylesheet";
    link.type = "text/css";
    link.href = cssUrl;
    document.head.appendChild(link);
  }
})();