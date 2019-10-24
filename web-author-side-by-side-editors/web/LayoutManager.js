goog.provide('sync.sidebyside.LayoutManager');

sync.sidebyside.LayoutManager.instance_ = null;

/**
 * Layout manager for two editors side by side.
 *
 * @constructor
 */
sync.sidebyside.LayoutManager = function() {};

/**
 * Create the diff-specific layout.
 */
sync.sidebyside.LayoutManager.prototype.createLayout = function(container) {
  this.container = container;

  goog.dom.classlist.add(container, 'side-by-side-wrapper');
  goog.dom.removeChildren(container);

  this.leftViewport = goog.dom.createDom('div', 'left-editor-wrapper');
  var leftEditorFrame = goog.dom.createDom('form', {
    id: 'left-editor-frame'
  });
  goog.dom.appendChild(this.leftViewport, leftEditorFrame);
  goog.dom.appendChild(container, this.leftViewport);
  this.leftDocumentContainer = leftEditorFrame;

  var separator = goog.dom.createDom('div', 'side-by-side-separator');
  goog.dom.appendChild(container, separator);

  this.rightViewport = goog.dom.createDom('div', 'right-editor-wrapper');
  var rightEditorFrame = goog.dom.createDom('form', {
    id: 'right-editor-frame'
  });
  goog.dom.appendChild(this.rightViewport, rightEditorFrame);
  goog.dom.appendChild(container, this.rightViewport);
  this.rightDocumentContainer = rightEditorFrame;
};

/**
 * @return {HTMLElement} The second editor container.
 */
sync.sidebyside.LayoutManager.prototype.getRightDocumentContainer = function () {
  return this.rightDocumentContainer;
};

/**
 * @return {HTMLElement} The primary editor container.
 */
sync.sidebyside.LayoutManager.prototype.getLeftDocumentContainer = function () {
  return this.leftDocumentContainer;
};

/**
 * @return {HTMLElement} The second editor viewport.
 */
sync.sidebyside.LayoutManager.prototype.getRightViewport = function () {
  return this.rightViewport;
};

/**
 * @return {HTMLElement} The primary editor container.
 */
sync.sidebyside.LayoutManager.prototype.getLeftViewport = function () {
  return this.leftViewport;
};

/**
 * @return {number} the header height.
 */
sync.sidebyside.LayoutManager.prototype.getHeaderHeight = function() {
  return this.container.getBoundingClientRect().y;
};

/**
 * Getter for singleton instance.
 */
sync.sidebyside.LayoutManager.getInstance = function() {
  if(!sync.sidebyside.LayoutManager.instance_) {
    sync.sidebyside.LayoutManager.instance_ = new sync.sidebyside.LayoutManager();
  }

  return sync.sidebyside.LayoutManager.instance_;
};