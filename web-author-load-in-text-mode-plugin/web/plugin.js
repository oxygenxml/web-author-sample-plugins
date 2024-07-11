workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, (e) => {
  console.log("Editor Loaded");
  e.editor.getEditingSupport().getActionsManager().invokeAction("Author/ShowXML");
  console.log(e.editor.getEditingSupport().getActionsManager())
});