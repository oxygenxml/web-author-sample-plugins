goog.events.listen(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function (e) {
  var editor = e.editor;
  var enterAction = editor.getActionsManager().getActionById('Author/Enter');
  var insertElementAction = editor.getActionsManager().getActionById('Author/InsertElement');

  editor.getActionsManager().registerAction('Author/InsertElement', new InsertAllElementsAction(enterAction, insertElementAction.getDisplayName()));
});

InsertAllElementsAction = function(enterAction, displayName) {
  sync.actions.AbstractAction.call(this);
  this.enterAction = enterAction;
  this.displayName = displayName;
};
InsertAllElementsAction.prototype = Object.create(sync.actions.AbstractAction.prototype);
InsertAllElementsAction.prototype.constructor = InsertAllElementsAction;

InsertAllElementsAction.prototype.getDisplayName = function() {
  return this.displayName;
};

InsertAllElementsAction.prototype.isEnabled = function() {
  return this.enterAction.isEnabled();
};

InsertAllElementsAction.prototype.actionPerformed = function(callback) {
  this.enterAction.actionPerformed(callback);
};