export default class TablePagination extends sync.formctrls.Enhancer {
  constructor(element, editingSupport) {
    super(element, editingSupport);

    this.currentPage_ = null;
    this.pageSize_ = 10;

    // Do not show validation errors decorators over the form control.
    this.formControl.style.borderBottom = "none";
    this.formControl.style.marginTop = "6px";

    this.modelChangedCallback_ = this.updatePaginationOnModelChanged_.bind(this);
    this.selectionChangedCallback_ = this.updateSelectedPageOnSelectionChange_.bind(this)
  }

  static tablePageNumberMap = new Map();

  /** @override */
  enterDocument(controller) {
    super.enterDocument(controller);

    this.tableDomElement_ = this.getParentNode();

    this.currentPage_ = TablePagination.tablePageNumberMap.get(this.tableDomElement_.id) || 0;
    this.editingSupport.listen(sync.api.AuthorEditingSupport.EventType.MODEL_CHANGED, this.modelChangedCallback_);


    if (this.editingSupport.getSelectionManager()) {
      this.editingSupport.getSelectionManager().listen(sync.api.SelectionManager.EventType.SELECTION_CHANGED, this.selectionChangedCallback_);
    } else {
      // fix npe caused by null SelectionManager at load time.
      workspace.listenOnce(sync.api.Workspace.EventType.EDITOR_LOADED, e => {
        this.editingSupport.getSelectionManager().listen(sync.api.SelectionManager.EventType.SELECTION_CHANGED, this.selectionChangedCallback_);
      });
    }

    this.refresh_();
  }

  updateSelectedPageOnSelectionChange_() {
    let selection = this.editingSupport.getSelectionManager().getSelection();
    let nodeAtSelection = selection.getNodeAtSelection();
    if (nodeAtSelection && this.tableDomElement_.compareDocumentPosition(nodeAtSelection) & window.Node.DOCUMENT_POSITION_CONTAINED_BY) {
      let tableRowAncestor = goog.dom.getAncestorByTagNameAndClass(nodeAtSelection.getHtmlNode(), "tr");
      let rows = this.getRowsHtmlElements_();
      let rowIdx = rows.indexOf(tableRowAncestor);
      if (rowIdx >= 0) {
        let newPage = Math.floor(rowIdx / this.pageSize_);
        if (this.currentPage_ !== newPage && this.canGoToPage_(newPage)) {
          this.setPage_(newPage);
        }
      }
    }
  }

  /** @override */
  exitDocument() {
    super.exitDocument();

    this.tableDomElement_ = null;
    this.editingSupport.unlisten(sync.api.AuthorEditingSupport.EventType.MODEL_CHANGED, this.modelChangedCallback_);
    this.editingSupport.getSelectionManager().unlisten(sync.api.SelectionManager.EventType.SELECTION_CHANGED, this.selectionChangedCallback_);
  }

  updatePaginationOnModelChanged_(modelChangedEvent) {
    if (modelChangedEvent.mutation) {
      let allChangedNodes = []
          .concat(modelChangedEvent.mutation.updated || [])
          .concat(modelChangedEvent.mutation.deleted || [])
          .concat(modelChangedEvent.mutation.inserted || []);
      for (let changedNode of allChangedNodes) {
        if (this.tableDomElement_.compareDocumentPosition(changedNode) & window.Node.DOCUMENT_POSITION_CONTAINED_BY) {
          this.refresh_();
          break;
        }
      }
    }
  }

  refresh_() {
    this.refreshPaginationHiddenRows_();
    this.refreshPaginationButtons_();
  }

  getRowsHtmlElements_() {
    let toReturn = [];

    let tableElement;
    for (let child of Array.from(this.tableDomElement_.getHtmlNode().children)) {
      if (child.localName === "table") {
        tableElement = child;
        break;
      }
    }

    let theadElement;
    for (let child of Array.from(tableElement.children)) {
      if (child.localName === "thead") {
        theadElement = child;
        break;
      }
    }

    let tbodyElement;
    for (let child of Array.from(tableElement.children)) {
      if (child.localName === "tbody") {
        tbodyElement = child;
        break;
      }
    }

    if (theadElement) {
      toReturn = toReturn.concat(Array.from(theadElement.children));
    }
    if (tbodyElement) {
      toReturn = toReturn.concat(Array.from(tbodyElement.children));
    }

    return toReturn;
  }

  refreshPaginationHiddenRows_() {
    let rows = this.getRowsHtmlElements_();
    for (let i = 0; i < rows.length; i++) {
      let row = rows[i];
      if (this.currentPage_ * this.pageSize_ <= i && i < (this.currentPage_ + 1) * this.pageSize_) {
        row.style.display = '';
      } else {
        row.style.display = 'none';
      }
    }
  }

  refreshPaginationButtons_() {
    goog.dom.removeChildren(this.formControl);
    if (this.getNoPages() < 2) {
      this.formControl.parentElement.style.display = "none";
      return;
    } else {
      this.formControl.parentElement.style.display = null;
    }

    let previousBtn = this.renderPageBtn_(this.currentPage_ - 1, "<");
    this.formControl.appendChild(previousBtn);


    let contextPageStart = Math.max(0, this.currentPage_ - 3);
    let contextPageEnd = Math.min(this.getNoPages(), this.currentPage_ + 3);

    if (contextPageStart > 0) {
      let btn = this.renderPageBtn_(0, 1);
      this.formControl.appendChild(btn);

      if (contextPageStart > 1) {
        this.formControl.appendChild(this.renderDots_());
      }
    }
    for (let i = contextPageStart; i < contextPageEnd; i++) {
      let btn = this.renderPageBtn_(i, i + 1);
      this.formControl.appendChild(btn);
    }

    if (contextPageEnd < this.getNoPages()) {
      if (contextPageEnd < this.getNoPages() - 1) {
        this.formControl.appendChild(this.renderDots_());
      }

      let btn = this.renderPageBtn_(this.getNoPages() - 1, this.getNoPages());
      this.formControl.appendChild(btn);
    }

    let nextBtn = this.renderPageBtn_(this.currentPage_ + 1, ">");
    this.formControl.appendChild(nextBtn);
  }

  setPage_(newPage) {
    if (this.canGoToPage_(newPage)) {
      this.currentPage_ = newPage;
      TablePagination.tablePageNumberMap.set(this.tableDomElement_.id, newPage);
      this.refresh_();
    }
  }

  canGoToPage_(pageNr) {
    let rows = this.getRowsHtmlElements_();
    let firstItemOnNewPage = pageNr * this.pageSize_;
    return pageNr >= 0 && firstItemOnNewPage < rows.length;
  }

  getNoPages() {
    let rows = this.getRowsHtmlElements_();
    return Math.ceil(rows.length / this.pageSize_);
  }

  renderPageBtn_(pageNr, pageText) {
    let btn = goog.dom.createDom('button', 'oxy-button', "" + pageText);
    if (pageNr < 0 || pageNr >= this.getNoPages()) {
      btn.disabled = true;
    } else {
      btn.title = "Go to page " + (pageNr + 1);
      btn.addEventListener("click", () => {
        this.setPage_(pageNr)
      });
      if (this.currentPage_ === pageNr) {
        btn.classList.add("oxy-button--primary");
      }
    }
    return btn;
  }

  renderDots_() {
    let dots = goog.dom.createDom("span", null, "...");
    dots.style.margin = "0 1em";
    dots.style.letterSpacing = "3px";

    // Do not show validation errors decorators over the form control.
    dots.style.borderBottom = "none";
    return dots;
  }
}