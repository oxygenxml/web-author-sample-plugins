(function () {
  goog.events.listen(workspace, sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
    var editor = e.editor;

    class PasteAsAction extends sync.actions.AbstractAction {
      constructor(displayName, shortcut) {
        super(shortcut);
        this.displayName_ = displayName;
      }

      getDisplayName() {
        return 'Paste as ' + this.displayName_ + '...';
      }

      actionPerformed(callback) {
        this.dialog = workspace.createDialog();
        this.dialog.setTitle('Paste as ' + this.displayName_);
        this.dialog.setButtonConfiguration(sync.api.Dialog.ButtonConfiguration.OK);

        let dialogElement = this.dialog.getElement();
        dialogElement.innerHTML = '<div>Paste here:<textarea style="display:block;width:500px;height:300px;">';
        let textArea = dialogElement.getElementsByTagName('textarea')[0];

        let submitDialog = () => {
          editor.getActionsManager().invokeOperation('com.oxygenxml.PasteAsOperation', {
            content: textArea.value,
            type: this.displayName_.toLowerCase()});
          this.dialog.dispose();
        };
        this.dialog.onSelect(submitDialog);
        dialogElement.addEventListener('keydown', e => {
          if (e.code === 'Enter' && e.ctrlKey) {
            submitDialog()
          }
        });
        callback();

        this.dialog.show();
        textArea.focus();
      }
    }

    editor.getActionsManager().registerAction('paste.as.xml', new PasteAsAction('XML', 'M1 M3 X'));
    editor.getActionsManager().registerAction('paste.as.text', new PasteAsAction('Text', 'M1 M3 T'));

    goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, function(e) {
      var contextualItems = e.actionsConfiguration.contextualItems;
      if (contextualItems) {
        var pasteSpecialIndex = contextualItems.findIndex(item => item.id === 'Author/PasteSpecial');
        contextualItems.splice(pasteSpecialIndex, 1, {
          id: 'paste.as.xml',
          type: 'action'
        }, {
          id: 'paste.as.text',
          type: 'action'
        });
      }
    });
  });
})();
