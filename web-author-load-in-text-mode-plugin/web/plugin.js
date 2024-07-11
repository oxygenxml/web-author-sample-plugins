workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, (e) => {
  let editingSupport = e.editor.getEditingSupport();
  if (editingSupport.getType() === sync.api.Editor.EditorTypes.AUTHOR) {
    editingSupport.getActionsManager().invokeAction("Author/ShowXML");
  }
});
