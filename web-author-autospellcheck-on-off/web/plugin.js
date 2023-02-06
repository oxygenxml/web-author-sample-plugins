(function () {
  /**
   * The action that toggles the autospellcheck state
   */
  class ToggleAutospellcheck extends sync.actions.AbstractAction {
    constructor() {
      super();
    };

    /** @override */
    getDisplayName() {
      return "Toggle autospellcheck";
    };

    /** @override */
    actionPerformed(callback) {
      workspace.setOption('spellcheck.enabled', this.isAutospellcheckOn() ? "false" : "true");
      this.updateIcon();
      callback();
    };

    isAutospellcheckOn() {
      return workspace.getOption('spellcheck.enabled') == "true";
    };

    renderLargeIcon() {
      this.iconDiv_ = goog.dom.createDom("div", 'ui-action-large-icon');
      this.updateIcon();
      return this.iconDiv_;
    }

    getIconUrl() {
      var path = '../rest/' + sync.api.Version + '/load/image/';
      var hidpi = sync.util.getHdpiFactor();
      if(hidpi) {
        path += hidpi;
      }
      path += '?url=/images/' + (this.isAutospellcheckOn() ? "CheckSpelling16.png" : "CheckSpellingDisabled16.png");
      return path;
    };

    updateIcon() {
      goog.style.setStyle(this.iconDiv_, 'backgroundImage', 'url("' + this.getIconUrl() + '")');
    }
  }


  /**
   * Add the toggle autospellcheck action to toolbar.
   */
  goog.events.listen(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
    let editor = e.editor;
    let actionId = 'toggle.autospellcheck';
    editor.getActionsManager().registerAction(actionId, new ToggleAutospellcheck(editor));

    goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, e => {
      let toolbar = e.actionsConfiguration.toolbars[0];
      if (toolbar && toolbar.name === 'Review') {
        toolbar.children.unshift({
          id: actionId,
          type: 'action'
        }, {type: 'sep'});
      }
    });
  });
})();
