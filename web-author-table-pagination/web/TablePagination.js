class TablePagination extends sync.formctrls.Enhancer {
  constructor(element, editingSupport) {
    super(element, editingSupport);

    this.currentPage_ = null;
    this.pageSize_ = 10;
    this.expanded_ = null; // will be initialized from the static map

    this.applyFormControlStyles_(this.formControl);

    this.modelChangedCallback_ = this.updatePaginationOnModelChanged_.bind(this);
    this.selectionChangedCallback_ = this.updateSelectedPageOnSelectionChange_.bind(this);
  }

  // Static maps to keep state per table DOM element.
  static tablePageNumberMap = new Map();
  static tableExpandedMap = new Map();

  // Apply custom styles to the form control (pagination control)
  applyFormControlStyles_(formControl) {
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

  /** @override */
  enterDocument(controller) {
    super.enterDocument(controller);

    this.tableDomElement_ = this.getParentNode();

    // Retrieve saved state or use defaults.
    this.currentPage_ = TablePagination.tablePageNumberMap.get(this.tableDomElement_.id) || 0;
    this.expanded_ = TablePagination.tableExpandedMap.get(this.tableDomElement_.id) || false;

    this.editingSupport.listen(sync.api.AuthorEditingSupport.EventType.MODEL_CHANGED, this.modelChangedCallback_);

    if (this.editingSupport.getSelectionManager()) {
      this.editingSupport.getSelectionManager().listen(sync.api.SelectionManager.EventType.SELECTION_CHANGED, this.selectionChangedCallback_);
    } else {
      // Fix npe caused by null SelectionManager at load time.
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

    let tbodyElement;
    for (let child of Array.from(tableElement.children)) {
      if (child.localName === "tbody") {
        tbodyElement = child;
        break;
      }
    }

    if (tbodyElement) {
      toReturn = toReturn.concat(Array.from(tbodyElement.children));
    }

    return toReturn;
  }

  refreshPaginationHiddenRows_() {
    let rows = this.getRowsHtmlElements_();
    if (this.expanded_) {
      // In expanded mode, show all rows.
      rows.forEach(row => row.style.display = '');
    } else {
      for (let i = 0; i < rows.length; i++) {
        if (this.currentPage_ * this.pageSize_ <= i && i < (this.currentPage_ + 1) * this.pageSize_) {
          rows[i].style.display = '';
        } else {
          rows[i].style.display = 'none';
        }
      }
    }
  }

  refreshPaginationButtons_() {
    goog.dom.removeChildren(this.formControl);

    if (this.expanded_) {
      // When expanded, only show the collapse button.
      let collapseBtn = this.renderCollapseBtn_();
      this.formControl.appendChild(collapseBtn);
      return;
    }

    // Only display pagination controls if there are at least 2 pages.
    if (this.getNoPages() < 2) {
      // Hide the pagination area if there's nothing to paginate.
      this.formControl.parentElement.style.display = "none";
      return;
    } else {
      this.formControl.parentElement.style.display = null;
    }

    // Render pagination navigation buttons.
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

    // Append the expand all pages button.
    let expandBtn = this.renderExpandBtn_();
    this.formControl.appendChild(expandBtn);
  }

  setSelectionForNewPage_(newPage) {
    const rows = this.getRowsHtmlElements_();
    // Calculate the index for the first row of the new page
    const firstRowIndex = newPage * this.pageSize_;
    if (rows[firstRowIndex]) {
      // get the first cell of the first row of the new page
      const firstCell = rows[firstRowIndex].querySelector("td");
      if (firstCell) {
        if (this.editingSupport.getSelectionManager()) {
          // set selection at the beginning of the first cell
          const domNode = this.editingSupport
              .getDocument()
              .createApiNodeOrParent(firstCell);
          let sel = this.editingSupport
              .getSelectionManager()
              .createEmptySelectionRelativeToNode(domNode, 0);
          this.editingSupport.getSelectionManager().setSelection(sel);

          // scroll table into view
          sel = this.editingSupport
              .getSelectionManager()
              .createEmptySelectionBeforeNode(this.tableDomElement_);
          this.editingSupport.getSelectionManager().scrollSelectionIntoView(sel);
        }
      }
    }
  }

  setPage_(newPage) {
    if (this.canGoToPage_(newPage)) {
      this.currentPage_ = newPage;
      TablePagination.tablePageNumberMap.set(this.tableDomElement_.id, newPage);
      this.refresh_();

      // Set selection to the first cell of the new page
      this.setSelectionForNewPage_(newPage);
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
        this.setPage_(pageNr);
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

  renderExpandBtn_() {
    let btn = goog.dom.createDom('button', 'oxy-button', "Display All Rows");
    btn.title = "Display the entire table";
    btn.addEventListener("click", () => {
      this.expanded_ = true;
      // Save the expanded state in the static map.
      TablePagination.tableExpandedMap.set(this.tableDomElement_.id, true);
      this.refresh_();
    });
    return btn;
  }

  renderCollapseBtn_() {
    let btn = goog.dom.createDom('button', 'oxy-button', "Collapse Table Rows (Enable Pagination)");
    btn.title = "Collapse the table into pages";
    btn.addEventListener("click", () => {
      this.expanded_ = false;
      // Save the collapsed state in the static map.
      TablePagination.tableExpandedMap.set(this.tableDomElement_.id, false);
      this.refresh_();
    });
    return btn;
  }
}

