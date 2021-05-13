goog.events.listen(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
  e.editor.getActionsManager().getActionById('Author/ViewFinal').actionPerformed(goog.nullFunction);
});