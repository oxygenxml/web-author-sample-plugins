package com.oxygenxml.sample.cals.tables;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;

/**
 * Rest safe operation
 */
@WebappRestSafe
@WebappCompatible
public class DeleteRowOperation extends ro.sync.ecss.extensions.commons.table.operations.cals.DeleteRowOperation {
    @Override
    public boolean performDeleteRows(AuthorAccess authorAccess,
        int startRowOffset, int endRowOffset) throws AuthorOperationException {
      TableCaretSetter tableCaretSetter = new TableCaretSetter(tableHelper);
      if (tableCaretSetter.isCaretBeforeTableContent(authorAccess)) {
        tableCaretSetter.moveCaretInsideFirstCell(authorAccess);
      }
      
      return super.performDeleteRows(authorAccess, startRowOffset, endRowOffset);
    }
}
