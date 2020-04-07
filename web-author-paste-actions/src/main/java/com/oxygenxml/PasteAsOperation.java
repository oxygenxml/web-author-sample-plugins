package com.oxygenxml;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.InvalidEditException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;

@WebappCompatible
@WebappRestSafe
public class PasteAsOperation extends AuthorOperationWithResult {

  @Override
  public String doOperation(AuthorDocumentModel model, ArgumentsMap args)
      throws AuthorOperationException {
    try {
      String cotentToPaste = (String) args.getArgumentValue("content");
      String type = (String) args.getArgumentValue("type");
      if (type.equals("xml")) {
        model.getActionsSupport().handleXmlPaste(cotentToPaste);
      } else {
        model.getActionsSupport().handleTextPaste(cotentToPaste);
      }
    } catch (InvalidEditException e) {
      throw new AuthorOperationException(e.getMessage(), e);
    };
    return null;
  }

}
