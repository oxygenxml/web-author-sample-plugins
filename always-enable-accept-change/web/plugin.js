workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  var editor = e.editor;
  editor.listen(sync.api.Editor.EventTypes.ACTIONS_LOADED, function(e) {
    var toolbars = e.actionsConfiguration.toolbars;
    for (const toolbar of toolbars) {
      if (toolbar.name === 'Review') {
        var action = editor.getActionsManager().getActionById('Author/AcceptChange');
        if (action && action.isTrackChangesForced_) {
          action.isTrackChangesForced_ = false;
          toolbar.children.unshift({
            type: 'action',
            id: 'Author/AcceptChange'
          });
        }
      }
    }
  });
});
