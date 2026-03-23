const ALLOWED_USERNAMES = new Set([
  'Alice Smith',
  'Bob Jones',
]);

const KNOWN_USERNAMES = new Set([
  ...ALLOWED_USERNAMES,
  'Carol White',
  'Dave Brown',
]);

/**
 * @param {!Object} comment The review comment.
 * @return {!Array<string>}
 */
function getMentionedUserNames(comment) {
  return comment.getMentionedUserNames();
}

/**
 * Format mentioned users for demo notifications.
 *
 * @param {!Array<string>} mentionedUsers Mentioned usernames.
 * @return {string}
 */
function formatMentionedUsers(mentionedUsers) {
  return mentionedUsers.join(', ');
}

/**
 * Log a demo action for mentioned users.
 *
 * @param {string} hookName The hook name.
 * @param {string} actionDescription The demo action.
 * @param {!Object} comment The review comment.
 * @return {!Promise<void>}
 */
function logMentionedUsersAction(hookName, actionDescription, comment) {
  const mentionedUsers = getMentionedUserNames(comment);
  if (!mentionedUsers.length) {
    return Promise.resolve();
  }

  console.log(hookName + ': ' + actionDescription + ' ' + formatMentionedUsers(mentionedUsers));
  return Promise.resolve();
}

/**
 * Reject comments that mention users who still need an invitation.
 */
class ValidatingCommentHook extends sync.api.author.ReviewCommentHook {
  /** @override */
  beforeCommentAdded(comment) {
    console.log('ValidatingCommentHook: beforeCommentAdded');
    return this.validateMentionedUsers_(comment);
  }

  /** @override */
  beforeCommentEdited(newComment, oldComment) {
    console.log('ValidatingCommentHook: beforeCommentEdited');
    return this.validateMentionedUsers_(newComment);
  }

  /**
   * @param {!Object} comment The review comment to validate.
   * @return {!Promise<void>}
   * @private
   */
  validateMentionedUsers_(comment) {
    const invalidUser = getMentionedUserNames(comment).find(username => !ALLOWED_USERNAMES.has(username));

    if (invalidUser) {
      if (!KNOWN_USERNAMES.has(invalidUser)) {
        return Promise.reject(new Error(
          'User "' + invalidUser + '" is not recognized.'));
      }
      const allowedList = [...ALLOWED_USERNAMES].join(' and ');
      return Promise.reject(new Error(
        'User "' + invalidUser + '" must be invited before saving. ' +
        'Only ' + allowedList + ' are currently allowed.'));
    }

    return Promise.resolve();
  }
}

/**
 * Demonstrates notification logic after a comment is stored.
 */
class NotifyingCommentHook extends sync.api.author.ReviewCommentHook {
  /** @override */
  commentAdded(comment) {
    console.log('NotifyingCommentHook: commentAdded');
    return this.notifyMentioned_(comment);
  }

  /** @override */
  commentEdited(newComment, oldComment) {
    console.log('NotifyingCommentHook: commentEdited');
    return this.notifyMentioned_(newComment);
  }

  /**
   * @param {!Object} comment The review comment.
   * @return {!Promise<void>}
   * @private
   */
  notifyMentioned_(comment) {
    return logMentionedUsersAction('NotifyingCommentHook', 'would notify', comment);
  }
}

/**
 * Demonstrates role updates for mentioned users after insertion.
 */
class RoleUpdateCommentHook extends sync.api.author.ReviewCommentHook {
  /** @override */
  commentAdded(comment) {
    const newUsers = getMentionedUserNames(comment).filter(u => !ALLOWED_USERNAMES.has(u));
    if (!newUsers.length) {
      return Promise.resolve();
    }
    console.log('RoleUpdateCommentHook: would grant reviewer role to ' + formatMentionedUsers(newUsers));
    return Promise.resolve();
  }

  /**
   * Role grants are intentionally skipped on edit: the role was already
   * granted when the comment was first added.
   *
   * @override
   */
  commentEdited(newComment, oldComment) {
    return Promise.resolve();
  }
}
