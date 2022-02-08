package com.oxygenxml;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.highlights.AuthorHighlighter;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Adds highlights on each paragraph from the document
 */
public class ParagraphsHighlighter {
  
  private AuthorHighlighter highlighter;
  private AuthorDocumentController controller;
  
  private static Map<String, String> HIGHLIGHTS_ATTRS = new HashMap<>();
  
  {
    HIGHLIGHTS_ATTRS.put("class", "grayBG");
    HIGHLIGHTS_ATTRS.put("type", "grayBGH");
  }
  
  /**
   * @param authorAccess Access to the non persistent highlighter. 
   */
  public ParagraphsHighlighter(AuthorAccess authorAccess) {
    highlighter = authorAccess.getEditorAccess().getHighlighter();
    controller = authorAccess.getDocumentController();
  }
  
  private void addHighlight(AuthorNode para) {
    try {
      highlighter.addHighlight(para.getStartOffset(), para.getEndOffset() + 1, null, HIGHLIGHTS_ATTRS);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Recompute the highlights.
   */
  public void recomputeHighlights() {
    highlighter.removeAllHighlights();
    AuthorNode[] allParas = findAllParas(controller);
    for (AuthorNode para : allParas) {
      this.addHighlight(para);
    }
  }
  
  /**
   * Find all paragraphs from the document.
   * 
   * @param controller Author document controller.
   * @return All paragraphs
   */
  private AuthorNode[] findAllParas(AuthorDocumentController controller) {
    AuthorNode[] allParas = new AuthorNode[0];
    try {
      allParas = controller.findNodesByXPath("//p", true, true, true);
    } catch (AuthorOperationException e) {
      e.printStackTrace();
    }
    return allParas;
  }
}
