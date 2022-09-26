let commentsOnlyEnabled = 'true' === new URLSearchParams(window.location.search).get('commentsOnly');

goog.events.listen(workspace, sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  if (commentsOnlyEnabled) {
    disableEditingControls(e);
  }
});

function disableEditingControls(e) {
  // Remove views that contain editing controls.
  workspace.getViewManager().removeView('attributes-panel-table');
  workspace.getViewManager().removeView('validation-panel-table');

  // Switch to review mode.
  e.options.modes = 'review';

  // Remove some actions from the UI.
  let editor = e.editor;
  let actionsManager = editor.getActionsManager();
  goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, function() {
    let actions = ['Author/TrackChanges',
      'Author/AcceptChange', 'Author/RejectChange',
      'Author/AcceptAllChanges', 'Author/RejectAllChanges',
      'Author/ShowXML',
      'Author/SpellingDialog',

      // Actions on breadcrumb
      'App/FocusAttributesPanel',
      'Author/DeleteElement',
      'Author/RenameElement',
      'Author/PasteSpecial',
      'Author/CutSpecial'
    ];
    actions.forEach(action => actionsManager.unregisterAction(action));
  })
}