package com.oxygenxml.username;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;

/**
 * InsertFragmentOperation that can expand the ${user.name} editor variable and works in both 
 * Oxygen XML Web Author and Oxygen XML Editor.
 * @author cristi_talau
 *
 */
public class InsertFragmentOperation extends ro.sync.ecss.extensions.commons.operations.InsertFragmentOperation{

  /**
   * The user name editor variable.
   */
  private static final String USER_NAME_VARIABLE = "${user.name}";

  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    super.doOperation(authorAccess, argumentName -> {
      if (ARGUMENT_FRAGMENT.equals(argumentName)) {
        String fragment = (String) args.getArgumentValue(argumentName);
        String userName = authorAccess.getReviewController().getReviewerAuthorName();
        return fragment.replace(USER_NAME_VARIABLE, userName);
      }
      return args.getArgumentValue(argumentName);
    });
  }
}
