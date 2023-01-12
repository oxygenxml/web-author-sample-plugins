package com.oxygenxml.sample.cals.tables;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;

/**
 * Rest safe operation
 */
@WebappRestSafe
@WebappCompatible
public class DeleteColumnOperation extends ro.sync.ecss.extensions.commons.table.operations.cals.DeleteColumnOperation {

  @Override
  protected void doOperationInternal(AuthorAccess authorAccess,
      ArgumentsMap args) throws AuthorOperationException {
    TableCaretSetter tableCaretSetter = new TableCaretSetter(tableHelper); 
    if (tableCaretSetter.isCaretBeforeTableContent(authorAccess)) {
      tableCaretSetter.moveCaretInsideFirstCell(authorAccess);
    }

    super.doOperationInternal(authorAccess, args);
  }
}
