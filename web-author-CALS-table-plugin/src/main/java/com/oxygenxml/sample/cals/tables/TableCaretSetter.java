package com.oxygenxml.sample.cals.tables;

import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;

/**
 * Moves the caret inside table e;ements.
 */
public class TableCaretSetter {
  
  private AuthorTableHelper tableHelper;

  /**
   * @param tableHelper Author table helper.
   */
  public TableCaretSetter(AuthorTableHelper tableHelper) {
    this.tableHelper = tableHelper;
  }
  
  /**
   * Move caret inside first cell of the current table.
   * 
   * @param authorAccess Access to methods related to the Author document model.
   */
  public void moveCaretInsideFirstCell(AuthorAccess authorAccess) {
    moveCaretInsideFirstTableRelatedElement(authorAccess, AuthorTableHelper.TYPE_CELL);
  }
  
  /**
   * Move caret inside first row of the current table.
   * 
   * @param authorAccess Access to methods related to the Author document model.
   */
  public void moveCaretInsideFirstRow(AuthorAccess authorAccess) {
    moveCaretInsideFirstTableRelatedElement(authorAccess, AuthorTableHelper.TYPE_ROW);
  }
  
  /**
   * Check if the caret is placed before a table content.
   * 
   * @param authorAccess Access to methods related to the Author document model.
   * @return <code>true</code> if the caret is placed before a table content.
   */
  public boolean isCaretBeforeTableContent(AuthorAccess authorAccess) {
    boolean isCaretBeforeTable = false;
    try {
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      AuthorNode nodeAtcaret = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      isCaretBeforeTable = tableHelper != null && tableHelper.isTable(nodeAtcaret) && 
          nodeAtcaret.getStartOffset() + 1 == caretOffset;
    } catch (BadLocationException e) {
      // Cannot determine if the caret node is a table
    }

    return isCaretBeforeTable;
  }

  private AuthorNode getTableAtCaret(AuthorAccess authorAccess) {
    AuthorNode tableAtCaret = null;
    try {
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      AuthorNode nodeAtcaret = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      if (tableHelper != null && tableHelper.isTable(nodeAtcaret)) {
        tableAtCaret = nodeAtcaret;
      }
    } catch (BadLocationException e) {
      // Cannot determine if the caret node is a table
    }

    return tableAtCaret;
  }


  private void moveCaretInsideFirstTableRelatedElement(
      AuthorAccess authorAccess, int tableElementType) {
    AuthorNode tableAtCaret = getTableAtCaret(authorAccess);
    if (tableAtCaret != null) {
      AuthorNode firstElem = getFirstElementOfType(tableAtCaret, tableElementType);
      if (firstElem != null) {
        authorAccess.getEditorAccess().setCaretPosition(firstElem.getStartOffset() + 1);
      }
    }
  }

  /**
   * Search for a child node {@link AuthorNode} with the specified type. 
   * 
   * @param node The starting node.
   * @param type The type of the searched node.
   * @return     The node of the given <code>type</code> or the <code>node</code> 
   * itself if the type matches.
   */
  private AuthorNode getFirstElementOfType(AuthorNode node, int type) {
    AuthorNode firstElemOfType = null;

    if (isTableElementOfType(node, type)) {
      firstElemOfType = node;
    } else if (node instanceof AuthorElement) {
      List<AuthorNode> contentNodes = ((AuthorElement) node).getContentNodes();
      for (AuthorNode contentNode : contentNodes) {
        firstElemOfType = getFirstElementOfType(contentNode, type);
        if (firstElemOfType != null) {
          break;
        }
      }
    }

    return firstElemOfType;
  }
  
  /**
   * Test if an {@link AuthorNode} is an element and it has one of the following types:
   * {@link AuthorTableHelper#TYPE_CELL}, {@link AuthorTableHelper#TYPE_ROW} or 
   * {@link AuthorTableHelper#TYPE_TABLE}.
   * 
   * @param node  The node to be checked.
   * @param type  The type to search for.
   * @return      <code>true</code> if the <code>node</code> is an element with the specified type.
   */
  private boolean isTableElementOfType(AuthorNode node, int type) {
    boolean toReturn = false;
    switch (type) {
      case AuthorTableHelper.TYPE_CELL:
        toReturn = tableHelper.isTableCell(node);
        break;
      case AuthorTableHelper.TYPE_ROW:
        toReturn = tableHelper.isTableRow(node);
        break;
      case AuthorTableHelper.TYPE_TABLE:
        toReturn = tableHelper.isTable(node);
        break;
    }
    return toReturn;
  }
}
