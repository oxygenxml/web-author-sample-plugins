goog.events.listen(
  workspace, sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
    e.options.autoSaveInterval=0;
    console.log("TODO: disable save and dirty status");
  }
);
