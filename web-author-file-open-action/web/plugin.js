workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  var editor = e.editor;
  class FileOpenAction extends sync.actions.AbstractAction {

    getDisplayName() {
      return 'Open...';
    }

    actionPerformed(callback) {
      var context = new sync.api.UrlChooser.Context(sync.api.UrlChooser.Type.GENERIC);
      var target = sync.api.Editor.LinkOpenedEvent.Target.BLANK;
      workspace.getUrlChooser().chooseUrl(context, url => {
        editor.openLink(new sync.api.Editor.LinkOpenedEvent(url, false, null, target));
      }, sync.api.UrlChooser.Purpose.CHOOSE);
      callback();
    }
  }
  editor.getActionsManager().registerAction('file.open', new FileOpenAction());
  
  editor.listen(sync.api.Editor.EventTypes.ACTIONS_LOADED, function(e) {
    e.actionsConfiguration.appendToToolbar('file.open', 'Builtin');
  });
});
