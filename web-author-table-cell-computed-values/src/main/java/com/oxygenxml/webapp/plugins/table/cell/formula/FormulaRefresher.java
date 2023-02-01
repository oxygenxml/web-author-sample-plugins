package com.oxygenxml.webapp.plugins.table.cell.formula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AuthorNode;

public class FormulaRefresher {
  public static Logger logger = LoggerFactory.getLogger(FormulaRefresher.class.getName());
  private final AuthorEditorAccess authorEditorAccess;
  private final AuthorDocumentController controller;

  public FormulaRefresher(AuthorAccess authorAccess) {
    this.authorEditorAccess = authorAccess.getEditorAccess();
    this.controller = authorAccess.getDocumentController();
  }

  public void refresh(AuthorNode node) {
    // Get the ancestor element with `display: table` style.
    AuthorNode table = node;
    while (table != null && !isTable(table)) {
      table = table.getParent();
    }
    if (table != null) {
      try {
        // Find all the processing-instructions in the table.
        AuthorNode[] processingInstructions = processingInstructions = controller
            .findNodesByXPath("//processing-instruction()", table, false, false, false, false);
        // Refresh the computed values of all the processing-instructions.
        for (AuthorNode processingInstruction : processingInstructions) {
          authorEditorAccess.refresh(processingInstruction);
        }
      } catch (AuthorOperationException e) {
        logger.error("Error finding formulas", e);
      }
    }
  }

  private boolean isTable(AuthorNode node) {
    return node.getType() == AuthorNode.NODE_TYPE_ELEMENT &&
        "table".equals(authorEditorAccess.getStyles(node).getDisplay());
  }
}
