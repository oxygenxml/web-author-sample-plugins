package com.oxygenxml;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;

/**
 * Operation that adds highlights on each paragraph.
 */
@WebappRestSafe
public class AddHighlightsOnParagraphsOperation implements AuthorOperation {

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    ParagraphsHighlighter highlighter = new ParagraphsHighlighter(authorAccess);
    highlighter.recomputeHighlights();
  }
  

  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

}
