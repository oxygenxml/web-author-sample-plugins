(function() {
  /**
   * Provides grouped user mention proposals for review comments.
   */
  class DemoUserMentionProposalsProvider extends sync.api.author.UserMentionProposalsProvider {
    /** @override */
    getUserMentionsProposals() {
      return Promise.resolve([
        {
          categoryName: 'Recent Collaborators',
          users: [
            { username: 'Alice Smith', email: 'alice@example.com' },
            { username: 'Bob Jones', email: 'bob@example.com' }
          ]
        },
        {
          categoryName: 'Team',
          users: [
            { username: 'Alice Smith', email: 'alice@corp.example' },
            { username: 'Carol White', email: 'carol@example.com' },
            { username: 'Dave Brown' }
          ]
        }
      ]);
    }
  }

  workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
    var editor = e.editor;
    var editingSupport = editor && editor.getEditingSupport ? editor.getEditingSupport() : null;
    if (!editingSupport) {
      return;
    }

    if (editingSupport.getType &&
      editingSupport.getType() !== sync.api.Editor.EditorTypes.AUTHOR) {
      return;
    }

    var reviewCommentsManager = editingSupport.getReviewCommentsManager ?
      editingSupport.getReviewCommentsManager() : null;
    if (!reviewCommentsManager) {
      return;
    }

    reviewCommentsManager.setUserMentionProposalsProvider(
      new DemoUserMentionProposalsProvider());
  });
})();
