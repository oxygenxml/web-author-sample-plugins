import TablePagination from "./TablePagination.js";

workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  e.editor.registerEnhancer(
      'com.oxygenxml.web_author.pagination.TablePaginationRenderer', TablePagination);
});
