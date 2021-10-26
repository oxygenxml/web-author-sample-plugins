function applicationStarted(pluginWorkspaceAccess) {
  pluginWorkspaceAccess.addEditingSessionLifecycleListener(
      new JavaAdapter(Packages.ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener, {
        editingSessionStarted: function (docId, authorDocumentModel) {
          var blockSuggestionDeletion = new JavaAdapter(Packages.ro.sync.ecss.extensions.api.AuthorDocumentFilter, {
            "delete": function(bypass, start, end, backspace) {
              if (shouldBlockDeletion(start, end)) {
                warnUser();
                return false;
              } else {
                return bypass['delete'](start, end, backspace);
              }
            },
            deleteNode: function(bypass, node) {
              if (shouldBlockDeletion(node.getStartOffset(), node.getEndOffset())) {
                warnUser();
                return false;
              } else {
                return bypass.deleteNode(node);
              }
            },
          });

          authorDocumentModel.getAuthorAccess().getDocumentController().setDocumentFilter(blockSuggestionDeletion);

          function shouldBlockDeletion(start, end) {
            var reviewController = authorDocumentModel.getReviewController();
            return reviewController.isTrackingChanges() && hasChangesMadeByOthers(start, end);
          }

          function hasChangesMadeByOthers(start, end) {
            var reviewController = authorDocumentModel.getReviewController();
            var changes = reviewController.getChangeHighlights(start, end) || [];
            var authors = getAuthors(changes);

            var currentAuthor = reviewController.getReviewerAuthorName();
            return authors.length > 1 || authors.length === 1 && authors[0] !== currentAuthor;
          }

          function getAuthors(changes) {
            var authors = [];
            for (var i = 0; i < changes.length; i++) {
              var changeAuthor = changes[i].getProperty("author");
              if (authors.indexOf(changeAuthor) === -1) {
                authors.push(changeAuthor);
              }
            }
            return authors;
          }

          function warnUser() {
            authorDocumentModel.getAuthorAccess().getWorkspaceAccess().showWarningMessage(
                "Cannot delete other user's suggestions. Use a comment to suggest your change.");
          }
        }
      }));
}

function applicationClosing(pluginWorkspaceAccess) {
  // Nothing to do.
}
