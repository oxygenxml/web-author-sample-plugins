(function () {
  /**
   * Renderer that will show the preview for a given URL.
   */
  class DocumentPreviewRenderer {
    constructor(editor) {
      this.editor = editor;
      this.previewElement = goog.dom.createDom('div', 'oxy-document-preview');
      this.previewElement.style.boxSizing = "border-box";
      this.documentUrl = null;
    }

    /**
     * @return {Element} The element where the documents are previewed.
     */
    getPreviewElement() {
      return this.previewElement;
    }

    /**
     * Preview the given URL.
     * @param {string} url URL of document to be previewed.
     */
    previewDocument(url) {
      if (this.documentUrl !== url) {
        this.clear();
        this.documentUrl = url;
        this.editor.previewAuthorDocument(url, this.previewElement)
          .then(goog.bind(function(editingSupport) {
            this.editingSupport_ = editingSupport;
        }, this)).thenCatch(function(e) {console.error(e)});
      }
    }

    /**
     * Clear the preview.
     */
    clear() {
      this.documentUrl = null;
      goog.dom.removeChildren(this.previewElement);
      if (this.editingSupport_) {
        this.editingSupport_.dispose();
        this.editingSupport_.getActionsManager().dispose();
      }
    }
  }


  /**
   * The action that preview documents inside a dialog.
   */
  class ShowDocumentPreviewAction extends sync.actions.AbstractAction {
    constructor(editor) {
      super();
      this.editor = editor;
      this.previewRenderer_ = new DocumentPreviewRenderer(editor);
    }

    /** @override */
    getDisplayName() {
      return "Preview document...";
    }

    /** @override */
    actionPerformed(callback) {
      this.callback_ = callback || goog.nullFunction;
      if (!this.isEnabled()) {
        this.callback_();
        return;
      }

      this.showFileBrowser();
    };

    /**
     * Show the file browser.
     */
    showFileBrowser() {
      this.previewRenderer_.clear();
      this.previewDialog_ && this.previewDialog_.hide();

      var urlChooser = workspace.getUrlChooser();
      var context = new sync.api.UrlChooser.Context(sync.api.UrlChooser.Type.EXTERNAL_REF);
      urlChooser.chooseUrl(context, goog.bind(this.onFileChoosen, this), sync.api.UrlChooser.Purpose.CHOOSE);
    };

    /**
     * Called when the user choose a document.
     * @param {string} url The choosen URL.
     */
    onFileChoosen(url) {
      if (url) {
        this.documentUrl = url;
        this.createPreviewDialog();
        this.showPreviewDocumentDialog(url);
      }
    };

    /**
     * Show the preview dialog.
     * @param {string} url The URL to be previewed.
     */
    showPreviewDocumentDialog(url) {
      this.previewRenderer_.previewDocument(url);
      this.previewDialog_.show();
    };

    /**
     * Called when the preview dialog is closed.
     * @param {string} key The close key.
     * @param {object} e
     */
    onPreviewDialogClosed(key, e) {
      this.previewRenderer_.clear();
      if (key === 'ok') {
        console.log("Document URL: ", this.documentUrl);
      } else {
        this.callback_();
      }
    }

    /**
     * Create the preview dialog.
     */
    createPreviewDialog() {
      if (!this.previewDialog_) {
        var cD = goog.dom.createDom;

        var dialog = workspace.createDialog();
        this.previewDialog_ = dialog;
        dialog.setPreferredSize(750, 700);
        dialog.setResizable(true);
        dialog.setTitle(this.dialogTitle_);
        dialog.setButtonConfiguration(sync.api.Dialog.ButtonConfiguration.OK_CANCEL);

        goog.dom.append(
          dialog.getElement(),
          cD('div', 'content-wrapper',
            this.previewRenderer_.getPreviewElement()
          )
        );

        this.previewDialog_.onSelect(goog.bind(this.onPreviewDialogClosed, this));
      }
    };
  }

  /**
   * @param {object} actionsConfiguration The actions configuration.
   * @return {boolean} true if the given actions loaded come from the framework.
   */
  function isFrameworkActions(actionsConfiguration) {
    var toolbars = actionsConfiguration.toolbars;
    for(var i = 0 ; i < toolbars.length; i++){
      if(toolbars[i].name !== "Builtin" && toolbars[i].name !== "Review") {
        return true;
      }
    }
    return false;
  }

  /**
   * Add the preview action to toolbar.
   */
  goog.events.listen(workspace, sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
    var editor = e.editor;

    var actionId = 'choose.link.target.action';
    editor.getActionsManager().registerAction(actionId, new ShowDocumentPreviewAction(editor));

    goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, function(e) {
      if(isFrameworkActions(e.actionsConfiguration)) {
        e.actionsConfiguration.toolbars[0].children.push({
          id: actionId,
          type: 'action'
        });
      }
    });
  });
})();
