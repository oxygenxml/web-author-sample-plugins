package com.oxygenxml.webapp.elements;

import static java.util.stream.Collectors.joining;

import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.ecss.extensions.api.webapp.cc.CCItemProxy;

/**
 * Operation to return elements to insert. 
 * @author cristi_talau
 */
@WebappRestSafe
public class GetElementsValidToInsert extends AuthorOperationWithResult {

  @Override
  public String doOperation(AuthorDocumentModel model, ArgumentsMap args)
      throws AuthorOperationException {
    try {
      List<CCItemProxy> forInsert = model.getContentCompletionManager().getProposedElementsForInsert(model.getSelectionModel());
      return forInsert.stream().map(CCItemProxy::getDisplayName).collect(joining(","));
    } catch (BadLocationException e) {
      throw new AuthorOperationException(e.getMessage(), e);
    }
  }
}
