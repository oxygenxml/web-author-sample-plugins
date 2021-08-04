package com.oxygenxml.webapp.elements;

import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.ecss.extensions.api.webapp.cc.CCItemProxy;
import ro.sync.ecss.extensions.api.webapp.cc.ContentCompletionManager;
import ro.sync.ecss.extensions.api.webapp.cc.ItemNotFoundException;

/**
 * Insert element from content completion.
 * 
 * @author cristi_talau
 */
@WebappRestSafe
public class InsertElementFromContentCompletion extends AuthorOperationWithResult {

  @Override
  public String doOperation(AuthorDocumentModel model, ArgumentsMap args) throws AuthorOperationException {
    String name = (String) args.getArgumentValue("name");
    try {
      ContentCompletionManager contentCompletionManager = model.getContentCompletionManager();
      List<CCItemProxy> forInsert = contentCompletionManager.getProposedElementsForInsert(model.getSelectionModel());
      CCItemProxy foundItem = forInsert.stream()
        .filter(item -> item.getDisplayName().equals(name))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
      contentCompletionManager.executeInsert(foundItem, model.getSelectionModel());
      return "success";
    } catch (BadLocationException e) {
      throw new AuthorOperationException(e.getMessage(), e);
    } catch (ItemNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
