goog.provide('sync.sidebyside.SideBySideEditingSupport');

/**
 * Diff editing support.
 *
 * @param {sync.api.Editor} editor The editor.
 *
 * @constructor
 */
sync.sidebyside.SideBySideEditingSupport = function(editor) {
  this.layoutManager_ = sync.sidebyside.LayoutManager.getInstance();

  this.editor_ = editor;
  this.primaryEditingSupport_ = editor.createBasicEditingSupport();

  goog.events.listen(editor, sync.api.Editor.EventTypes.LINK_OPENED, function(e) {
    /* Remove remove side by side specific URL parameter*/
    delete e.params.rightUrl;
  });

  sync.api.WrapperAuthorEditingSupport.call(this, this.primaryEditingSupport_);
};
goog.inherits(sync.sidebyside.SideBySideEditingSupport, sync.api.WrapperAuthorEditingSupport);

/** @override */
sync.sidebyside.SideBySideEditingSupport.prototype.load = function(newDoc, docContainer, problemReporter) {
  var container = docContainer.parentElement;
  this.layoutManager_.createLayout(container);

  var leftEditorContainer = this.layoutManager_.getLeftDocumentContainer();
  var rightEditorContainer = this.layoutManager_.getRightDocumentContainer();
  return this.primaryEditingSupport_.load(newDoc, leftEditorContainer, problemReporter)
    .then(goog.bind(this.loadPreviewEditingSupport, this, this.editor_, rightEditorContainer))
};

/**
 * Load the secondary editing support.
 *
 * @return {goog.Promise<sync.api.EditingSupport>} a promise the resolves with the secondary editing support.
 */
sync.sidebyside.SideBySideEditingSupport.prototype.loadPreviewEditingSupport = function(editor, secondaryDocContainer) {
  var options = editor.options;
  var secondaryEditorOptions = {
    url: options.rightUrl,
    ditamap: options.ditamap || options.guessedDitamap
  };

  return editor.loadPreview(secondaryDocContainer, secondaryEditorOptions);
};

/** @override */
sync.sidebyside.SideBySideEditingSupport.prototype.getType = function () {
  return 'side-by-side';
};
