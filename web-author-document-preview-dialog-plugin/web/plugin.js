(function () {
  /**
   * The action that previews documents inside a dialog.
   */
  class ShowDocumentPreviewAction extends sync.actions.AbstractAction {
    constructor(editor) {
      super();
      this.editor = editor;
    }

    /** @override */
    getDisplayName() {
      return "Preview document...";
    }

    /** @override */
    actionPerformed(callback) {
      let context = new sync.api.UrlChooser.Context(sync.api.UrlChooser.Type.EXTERNAL_REF);
      workspace.getUrlChooser().chooseUrl(context, url => {
        if (url) {
          this.showPreviewDocumentDialog(url, callback);
        } else {
          callback();
        }
      }, sync.api.UrlChooser.Purpose.CHOOSE);
    };


    /**
     * Show the preview dialog.
     * @param {string} url The URL to be previewed.
     * @param {function} callback The action callback.
     */
    showPreviewDocumentDialog(url, callback) {
      let dialog = workspace.createDialog();
      dialog.setPreferredSize(750, 700);
      dialog.setResizable(true);
      dialog.setTitle('Preview');

      let editingSupport = null;
      dialog.onSelect(() => {
        callback();
        editingSupport.dispose();
        editingSupport.getActionsManager().dispose();
        dialog.dispose()
      });

      dialog.getElement().classList.add('oxy-document-preview');
      this.editor.previewAuthorDocument(url, dialog.getElement())
          .then(es => editingSupport = es)
          .thenCatch(console.error);

      dialog.show();
    };
  }

  /**
   * Add the preview action to toolbar.
   */
  goog.events.listen(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
    let editor = e.editor;
    let actionId = 'choose.link.target.action';
    editor.getActionsManager().registerAction(actionId, new ShowDocumentPreviewAction(editor));

    goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, e => {
      let toolbar = e.actionsConfiguration.toolbars[0];
      if (toolbar && toolbar.name === 'Builtin') {
        toolbar.children.push({
          id: actionId,
          type: 'action'
        });
      }
    });
  });
})();
