class PreviewAction extends sync.actions.AbstractAction {
  /** 
   * @type {sync.api.Editor} editor The editor object.
   */
  constructor(editor) {
    
    super(editor, 'M1 L');
    this.editor = editor;
  }

  /** @override */
  getDisplayName() {
    return 'Preview dialog';
  }

  /** @override */
  isEnabled() {
    
    return true;
  }

  /** @override */
  actionPerformed(callback) {
    if (!workspace.currentEditor) {
      console.error("No current editor available");
      callback && callback();
      return;
    }

    
    const dialog = workspace.createDialog();
    dialog.setPreferredSize(750, 700);
    dialog.setResizable(true);
    dialog.setTitle('Preview');

    const fragmentXML = `
<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">
<topic id="test">
  <title>Sample fragment</title>
  <body>
    <p>Test preview</p>
  </body>
</topic>
`;

    workspace.currentEditor.loadPreview(dialog.getElement(), {
      url: "http://placeholder.txt", 
      content: fragmentXML
    });

    dialog.show();

    callback && callback();
  }
}

workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  var editor = e.editor;
  editor.getActionsManager().registerAction('preview.dialog', new PreviewAction(editor));
  
  addToDitaToolbar(editor, 'preview.dialog');
});

workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
  var editor = e.editor;

  editor.getSelectionManager().listen(
    sync.api.SelectionManager.EventType.SELECTION_CHANGED,
    function() {
      editor.getActionsManager().refreshActionsStatus('preview.dialog');
    }
  );
});

function addToDitaToolbar(editor, actionId) {
  editor.listen(sync.api.Editor.EventTypes.ACTIONS_LOADED, function(e) {
    var actionsConfig = e.actionsConfiguration;
    var ditaToolbar = null;

    if (actionsConfig.toolbars) {
      for (var i = 0; i < actionsConfig.toolbars.length; i++) {
        var toolbar = actionsConfig.toolbars[i];
        if (toolbar.name === "DITA") {
          ditaToolbar = toolbar;
          break;
        }
      }
    }

    if (ditaToolbar) {
      ditaToolbar.children.push({
        id: actionId,
        type: "action"
      });
    }
  });
}
