/**
 * @param {HTMLElement} element The element to enhance.
 * @param editor is the editor.
 * @constructor
 */
sync.formctrls.ScrollFormControl = function(element, editingSupport) {
  sync.formctrls.Enhancer.call(this, element, editingSupport);
};
goog.inherits(sync.formctrls.ScrollFormControl, sync.formctrls.Enhancer);

/**
 * Registers the listeners for the current Text.
 *
 * @override
 */
sync.formctrls.ScrollFormControl.prototype.enhanceDom = function() {
  this.span = this.formControl.querySelector('*[data-arg-element-to-scroll]');
  this.elementToScrollId = this.span.dataset.argElementToScroll;

  var button = goog.dom.createDom('div', 'side-by-side-scroll-button');
  goog.dom.appendChild(this.span, button);
  goog.events.listen(button, goog.events.EventType.CLICK, goog.bind(this.scrollOtherEditor, this));
};

/**
 * Scroll other editor to pair form control.
 */
sync.formctrls.ScrollFormControl.prototype.scrollOtherEditor = function() {
  var layoutManager = sync.sidebyside.LayoutManager.getInstance();

  var leftViewport = layoutManager.getLeftViewport();
  var rightViewport = layoutManager.getRightViewport();

  var isInLeftEditor = goog.dom.contains(leftViewport, this.formControl);

  var pairViewport = isInLeftEditor ? rightViewport : leftViewport;
  var pairSpan = pairViewport.querySelector(
    '*[data-arg-element-to-scroll="' + this.elementToScrollId +'"]');

  if(pairSpan) {
    var headerHeight = layoutManager.getHeaderHeight();

    pairViewport.scrollTop = pairSpan.offsetTop - this.span.getBoundingClientRect().y + headerHeight;
  } else {
    // could not locate corresponding button from the other document.
  }
};