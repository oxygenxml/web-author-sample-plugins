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

    renderSmallIcon() {
      this.smallIconDiv_ = sync.util.renderSmallIcon(this.getIconUrl());
      return this.smallIconDiv_;
    }

    getIconUrl() {
      var hidpi = sync.util.getHdpiFactor();
      var iconUrl = "/images/" + (this.isAutospellcheckOn() ? "CheckSpelling16.png" : "CheckSpellingDisabled16.png");
      return sync.util.image.getImageUrl(iconUrl, hidpi);
    }

    updateIcon() {
      goog.style.setStyle(this.smallIconDiv_, 'backgroundImage', 'url("' + this.getIconUrl() + '")');
    }

    /** @override */
    getSmallIcon(opt_dpi) {
      var iconUrl = "/images/" + (this.isAutospellcheckOn() ? "CheckSpelling16.png" : "CheckSpellingDisabled16.png");
      return sync.util.image.getImageUrl(iconUrl, opt_dpi);
    };
  }


  /**
   * Add the toggle autospellcheck action to toolbar.
   */
  goog.events.listen(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
    let editor = e.editor;
    let actionId = 'toggle.autospellcheck';
    editor.getActionsManager().registerAction(actionId, new ToggleAutospellcheck(editor));

    goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, e => {
      console.log(e.actionsConfiguration)
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
