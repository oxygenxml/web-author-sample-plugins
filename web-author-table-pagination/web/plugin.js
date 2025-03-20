workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  e.editor.registerEnhancer(
      'com.oxygenxml.web_author.pagination.TablePaginationRenderer', TablePagination);
});


// Disable table drag handler for performance
workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, (e) => {
  sync.table.TableDragHandleInstaller.prototype.attach = () => {};
});