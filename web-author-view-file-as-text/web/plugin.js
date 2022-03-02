(function () {
  workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function({editor}) {
    class ViewFileAsText extends sync.actions.AbstractAction {
      getDisplayName() {
        return 'View file as text';
      }

      actionPerformed(callback) {
        var chooserContext = new sync.api.UrlChooser.Context(sync.api.UrlChooser.Type.GENERIC)
        workspace.getUrlChooser().chooseUrl(chooserContext, url => {
          if (url) {
            editor.getActionsManager().invokeOperation('com.oxygenxml.LoadFile', {url}, (_, text) => {
              alert(text)
            });
          }
        });
        callback();
      }
    }

    editor.getActionsManager().registerAction('view.file.as.text', new ViewFileAsText());

    goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, function ({actionsConfiguration}) {
      actionsConfiguration.appendToToolbar('view.file.as.text', 'Builtin');
    });
  });
})();
