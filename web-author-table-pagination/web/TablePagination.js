function TablePagination(element, editingSupport) {
  sync.formctrls.Enhancer.call(this, element, editingSupport);

  this.currentPage_ = null;
  this.pageSize_ = 10;
  this.expanded_ = null; // will be initialized from the static map
  
  this.applyFormControlStyles_(this.formControl);

  this.modelChangedCallback_ = this.updatePaginationOnModelChanged_.bind(this);
  this.selectionChangedCallback_ = this.updateSelectedPageOnSelectionChange_.bind(this);
}
goog.inherits(TablePagination, sync.formctrls.Enhancer);

// Static maps to keep state per table DOM element.
TablePagination.tablePageNumberMap = new Map();
TablePagination.tableExpandedMap = new Map();

TablePagination.prototype.applyFormControlStyles_ = function(formControl) {
  // Do not show validation errors decorators over the form control.
  formControl.style.borderBottom = "none";

  // Make it stick to the bottom of the table
  formControl.style.position = "sticky";
  formControl.style.bottom = "0";

  // Display flex to center the buttons
  formControl.style.display = "flex";
  formControl.style.flexWrap = "wrap";
  formControl.style.alignItems = "center";
  formControl.style.justifyContent = "center";

  // It should not be obscured by other elements
  formControl.style.zIndex = "100";

  // Other styles
  formControl.style.margin = "10px 0";
  formControl.style.padding = "10px";
  formControl.style.background = "white";
  formControl.style.border = "1px solid lightgray";
}

TablePagination.prototype.enterDocument = function(controller) {
  sync.formctrls.Enhancer.prototype.enterDocument.call(this, controller);

  this.tableDomElement_ = this.getParentNode();

  // Retrieve saved state or use defaults.
  this.currentPage_ =
    TablePagination.tablePageNumberMap.get(this.tableDomElement_.id) || 0;
  this.expanded_ =
    TablePagination.tableExpandedMap.get(this.tableDomElement_.id) || false;

  this.editingSupport.listen(
    sync.api.AuthorEditingSupport.EventType.MODEL_CHANGED,
    this.modelChangedCallback_
  );

  if (this.editingSupport.getSelectionManager()) {
    this.editingSupport
      .getSelectionManager()
      .listen(
        sync.api.SelectionManager.EventType.SELECTION_CHANGED,
        this.selectionChangedCallback_
      );
  } else {
    // Fix npe caused by null SelectionManager at load time.
    workspace.listenOnce(
      sync.api.Workspace.EventType.EDITOR_LOADED,
      function (e) {
        this.editingSupport
          .getSelectionManager()
          .listen(
            sync.api.SelectionManager.EventType.SELECTION_CHANGED,
            this.selectionChangedCallback_
          );
      }.bind(this)
    );
  }

  this.refresh_();
};

TablePagination.prototype.updateSelectedPageOnSelectionChange_ = function() {
  var selection = this.editingSupport.getSelectionManager().getSelection();
  var nodeAtSelection = selection.getNodeAtSelection();
  if (
    nodeAtSelection &&
    this.tableDomElement_.compareDocumentPosition(nodeAtSelection) &
      window.Node.DOCUMENT_POSITION_CONTAINED_BY
  ) {
    var tableRowAncestor = goog.dom.getAncestorByTagNameAndClass(
      nodeAtSelection.getHtmlNode(),
      "tr"
    );
    var rows = this.getRowsHtmlElements_();
    var rowIdx = rows.indexOf(tableRowAncestor);
    if (rowIdx >= 0) {
      var newPage = Math.floor(rowIdx / this.pageSize_);
      if (this.currentPage_ !== newPage && this.canGoToPage_(newPage)) {
        this.setPage_(newPage);
      }
    }
  }
};

TablePagination.prototype.exitDocument = function() {
  sync.formctrls.Enhancer.prototype.exitDocument.call(this);

  this.tableDomElement_ = null;
  this.editingSupport.unlisten(
    sync.api.AuthorEditingSupport.EventType.MODEL_CHANGED,
    this.modelChangedCallback_
  );
  this.editingSupport
    .getSelectionManager()
    .unlisten(
      sync.api.SelectionManager.EventType.SELECTION_CHANGED,
      this.selectionChangedCallback_
    );
};

TablePagination.prototype.updatePaginationOnModelChanged_ = function(
  modelChangedEvent
) {
  if (modelChangedEvent.mutation) {
    var allChangedNodes = []
      .concat(modelChangedEvent.mutation.updated || [])
      .concat(modelChangedEvent.mutation.deleted || [])
      .concat(modelChangedEvent.mutation.inserted || []);
    for (var i = 0; i < allChangedNodes.length; i++) {
      var changedNode = allChangedNodes[i];
      if (
        this.tableDomElement_.compareDocumentPosition(changedNode) &
        window.Node.DOCUMENT_POSITION_CONTAINED_BY
      ) {
        this.refresh_();
        break;
      }
    }
  }
};

TablePagination.prototype.refresh_ = function() {
  this.refreshPaginationHiddenRows_();
  this.refreshPaginationButtons_();
};

TablePagination.prototype.getRowsHtmlElements_ = function() {
  var toReturn = [];

  var tableElement;
  var children = Array.prototype.slice.call(
    this.tableDomElement_.getHtmlNode().children
  );
  for (var i = 0; i < children.length; i++) {
    if (children[i].localName === "table") {
      tableElement = children[i];
      break;
    }
  }

  var tbodyElement;
  var tableChildren = Array.prototype.slice.call(tableElement.children);
  for (i = 0; i < tableChildren.length; i++) {
    if (tableChildren[i].localName === "tbody") {
      tbodyElement = tableChildren[i];
      break;
    }
  }

  if (tbodyElement) {
    toReturn = toReturn.concat(
      Array.prototype.slice.call(tbodyElement.children)
    );
  }

  return toReturn;
};

TablePagination.prototype.refreshPaginationHiddenRows_ = function() {
  var rows = this.getRowsHtmlElements_();
  if (this.expanded_) {
    // In expanded mode, show all rows.
    rows.forEach(function (row) {
      row.style.display = "";
    });
  } else {
    for (var i = 0; i < rows.length; i++) {
      if (
        this.currentPage_ * this.pageSize_ <= i &&
        i < (this.currentPage_ + 1) * this.pageSize_
      ) {
        rows[i].style.display = "";
      } else {
        rows[i].style.display = "none";
      }
    }
  }
};

TablePagination.prototype.refreshPaginationButtons_ = function() {
  goog.dom.removeChildren(this.formControl);

  if (this.expanded_) {
    // When expanded, only show the collapse button.
    var collapseBtn = this.renderCollapseBtn_();
    this.formControl.appendChild(collapseBtn);
    return;
  }

  // Only display pagination controls if there are at least 2 pages.
  if (this.getNoPages() < 2) {
    this.formControl.parentElement.style.display = "none";
    return;
  } else {
    this.formControl.parentElement.style.display = null;
  }

  var previousBtn = this.renderPageBtn_(this.currentPage_ - 1, "<");
  this.formControl.appendChild(previousBtn);

  var contextPageStart = Math.max(0, this.currentPage_ - 3);
  var contextPageEnd = Math.min(this.getNoPages(), this.currentPage_ + 3);

  if (contextPageStart > 0) {
    var btn = this.renderPageBtn_(0, 1);
    this.formControl.appendChild(btn);

    if (contextPageStart > 1) {
      this.formControl.appendChild(this.renderDots_());
    }
  }
  for (var i = contextPageStart; i < contextPageEnd; i++) {
    btn = this.renderPageBtn_(i, i + 1);
    this.formControl.appendChild(btn);
  }

  if (contextPageEnd < this.getNoPages()) {
    if (contextPageEnd < this.getNoPages() - 1) {
      this.formControl.appendChild(this.renderDots_());
    }

    btn = this.renderPageBtn_(this.getNoPages() - 1, this.getNoPages());
    this.formControl.appendChild(btn);
  }

  var nextBtn = this.renderPageBtn_(this.currentPage_ + 1, ">");
  this.formControl.appendChild(nextBtn);

  // Append the expand all pages button.
  var expandBtn = this.renderExpandBtn_();
  this.formControl.appendChild(expandBtn);
};

TablePagination.prototype.setSelectionForNewPage_ = function(newPage) {
  var rows = this.getRowsHtmlElements_();
  // Calculate the index for the first row of the new page
  var firstRowIndex = newPage * this.pageSize_;
  if (rows[firstRowIndex]) {
    // get the first cell of the first row of the new page
    var firstCell = rows[firstRowIndex].querySelector("td");
    if (firstCell) {
      if (this.editingSupport.getSelectionManager()) {
        // set selection at the beginning of the first cell
        let domNode = this.editingSupport.getDocument().createApiNodeOrParent(firstCell);
        var sel = this.editingSupport.getSelectionManager().createEmptySelectionRelativeToNode(domNode, 0);
        this.editingSupport.getSelectionManager().setSelection(sel);

        // scroll table into view
        sel = this.editingSupport.getSelectionManager().createEmptySelectionBeforeNode(this.tableDomElement_);
        this.editingSupport.getSelectionManager().scrollSelectionIntoView(sel);
      }
    }
  }
}

TablePagination.prototype.setPage_ = function(newPage) {
  if (this.canGoToPage_(newPage)) {
    this.currentPage_ = newPage;
    TablePagination.tablePageNumberMap.set(this.tableDomElement_.id, newPage);
    this.refresh_();

    // Set selection to the first cell of the new page
    this.setSelectionForNewPage_(newPage);
  }
};

TablePagination.prototype.canGoToPage_ = function(pageNr) {
  var rows = this.getRowsHtmlElements_();
  var firstItemOnNewPage = pageNr * this.pageSize_;
  return pageNr >= 0 && firstItemOnNewPage < rows.length;
};

TablePagination.prototype.getNoPages = function() {
  var rows = this.getRowsHtmlElements_();
  return Math.ceil(rows.length / this.pageSize_);
};

TablePagination.prototype.renderPageBtn_ = function(pageNr, pageText) {
  var btn = goog.dom.createDom("button", "oxy-button", "" + pageText);
  if (pageNr < 0 || pageNr >= this.getNoPages()) {
    btn.disabled = true;
  } else {
    btn.title = "Go to page " + (pageNr + 1);
    btn.addEventListener(
      "click",
      function () {
        this.setPage_(pageNr);
      }.bind(this)
    );
    if (this.currentPage_ === pageNr) {
      btn.classList.add("oxy-button--primary");
    }
  }
  return btn;
};

TablePagination.prototype.renderDots_ = function() {
  var dots = goog.dom.createDom("span", null, "...");
  dots.style.margin = "0 1em";
  dots.style.letterSpacing = "3px";
  dots.style.borderBottom = "none";
  return dots;
};

TablePagination.prototype.renderExpandBtn_ = function() {
  var btn = goog.dom.createDom("button", "oxy-button", "Display All Rows");
  btn.title = "Display the entire table";
  btn.addEventListener(
    "click",
    function () {
      this.expanded_ = true;
      TablePagination.tableExpandedMap.set(this.tableDomElement_.id, true);
      this.refresh_();
    }.bind(this)
  );
  return btn;
};

TablePagination.prototype.renderCollapseBtn_ = function() {
  var btn = goog.dom.createDom(
    "button",
    "oxy-button",
    "Collapse Table Rows (Enable Pagination)"
  );
  btn.title = "Collapse the table into pages";
  btn.addEventListener(
    "click",
    function () {
      this.expanded_ = false;
      TablePagination.tableExpandedMap.set(this.tableDomElement_.id, false);
      this.refresh_();
    }.bind(this)
  );
  return btn;
};
