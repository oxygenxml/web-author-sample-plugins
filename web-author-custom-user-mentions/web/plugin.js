/**
 * Provides grouped user mention proposals for review comments.
 */
class DemoUserMentionProposalsProvider extends sync.api.author.UserMentionProposalsProvider {
  /** @override */
  getUserMentionsProposals() {
    return Promise.resolve([
      {
        categoryName: 'Users Who Already Have Access',
        users: [
          { username: 'Alice Smith', email: 'alice@example.com' },
          { username: 'Alice Smith', email: 'alice@corp.example' },
          { username: 'Bob Jones', email: 'bob@example.com' }
        ]
      },
      {
        categoryName: 'Users Who Need To Be Invited',
        users: [
          { username: 'Carol White', email: 'carol@example.com' },
          { username: 'Dave Brown' }
        ]
      }
    ]);
  }
}

workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
  const editingSupport = e.editor.getEditingSupport();

  if (editingSupport.getType() !== sync.api.Editor.EditorTypes.AUTHOR) {
    return;
  }

  const reviewCommentsManager = editingSupport.getReviewCommentsManager();

  reviewCommentsManager.setUserMentionProposalsProvider(
    new DemoUserMentionProposalsProvider());
  reviewCommentsManager.registerReviewCommentHook(
    new ValidatingCommentHook());
  reviewCommentsManager.registerReviewCommentHook(
    new NotifyingCommentHook());
  reviewCommentsManager.registerReviewCommentHook(
    new RoleUpdateCommentHook());
});
