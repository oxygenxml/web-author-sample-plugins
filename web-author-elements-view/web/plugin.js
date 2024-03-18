(function () {

  class ElementsViewRenderer extends sync.view.ViewRenderer {

    constructor() {
      super();
      this.selectionListener_ = this.updateOnSelectionChanged.bind(this);
      this.open_ = false;
      this.renderTimer_ = 0;
    }

    editorChanged(editor) {
      if (this.selectionManager_) {
        this.selectionManager_.unlisten(sync.api.SelectionManager.EventType.SELECTION_CHANGED, this.selectionListener_);
      }
      this.selectionManager_ = editor.getSelectionManager();
      this.selectionManager_.listen(sync.api.SelectionManager.EventType.SELECTION_CHANGED, this.selectionListener_);

      this.actionsManager_ = editor.getActionsManager();
      if (this.open_) {
        this.update();
      }
    }

    install(element) {
      this.container_ = element;
    }

    opened() {
      this.open_ = true;
      this.update();
    }

    closed() {
      this.open_ = false;
    }

    updateOnSelectionChanged() {
      if (this.open_) {
        clearTimeout(this.renderTimeout_);
        this.renderTimeout_ = setTimeout(() => this.update(), 100);
      }
    }

    update() {
      if (this.actionsManager_) {
        this.actionsManager_.invokeOperation(
            'com.oxygenxml.webapp.elements.GetElementsValidToInsert', {},
            (e, elems) => this.renderElements(elems),
            null,
            true);
      }
    }

    renderElements(commaSeparatedNames) {
      this.container_.textContent = '';
      commaSeparatedNames.split(',')
          .map(name => this.renderElement(name))
          .forEach(button => this.container_.appendChild(button));
    }

    renderElement(name) {
      let button = document.createElement('button');

      button.style.display = 'block';
      button.style.marginTop = '5px';
      button.style.marginBottom = '5px';
      button.style.backgroundColor = 'lightgray';
      button.style.padding = '4px';
      button.style.width = '100%';
      button.style.textAlign = 'left';

      button.textContent = name;
      button.onclick = () => this.insertElement(name);
      return button;
    }

    insertElement(name) {
      this.actionsManager_.invokeOperation(
          'com.oxygenxml.webapp.elements.InsertElementFromContentCompletion',
          {name: name},
          () => this.update());
    }


    getTitle() {
      return 'Elements';
    }
  }

  workspace.getViewManager().addView('elements');
  workspace.getViewManager().installView('elements', new ElementsViewRenderer());
})();
