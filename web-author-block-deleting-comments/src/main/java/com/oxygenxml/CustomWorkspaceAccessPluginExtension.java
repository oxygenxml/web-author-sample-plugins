package com.oxygenxml;

import ro.sync.ecss.extensions.api.AuthorDocumentFilter;
import ro.sync.ecss.extensions.api.AuthorDocumentFilterBypass;
import ro.sync.ecss.extensions.api.AuthorReviewController;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlight;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Custom extension that adds a document filter
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {
  
  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace workspace = (WebappPluginWorkspace) pluginWorkspaceAccess;
    
    workspace.addEditingSessionLifecycleListener(new WebappEditingSessionLifecycleListener() {
      
      @Override
      public void editingSessionStarted(String sessionId, AuthorDocumentModel documentModel) {
        AuthorReviewController reviewController = documentModel.getAuthorAccess().getReviewController();
        BlockDeleteOthersCommentsHelper blockDeleteCommHelper = new BlockDeleteOthersCommentsHelper(reviewController);
        
        documentModel.getAuthorDocumentController().setDocumentFilter(new AuthorDocumentFilter() {
          @Override
          public boolean removeMarker(AuthorDocumentFilterBypass filterBypass, AuthorPersistentHighlight marker) {
            if (blockDeleteCommHelper.isMarkerRelatedToOthersComments(marker)) {
              notifyUserThatCommentCannotBeRemoved(documentModel);
              return false;
            }
            
            return super.removeMarker(filterBypass, marker);
          }

          private void notifyUserThatCommentCannotBeRemoved(AuthorDocumentModel documentModel) {
            documentModel.getAuthorAccess().getWorkspaceAccess().showWarningMessage("Cannot delete another user's comments.");
          }
          
          @Override
          public boolean delete(AuthorDocumentFilterBypass filterBypass, int startOffset, int endOffset, boolean withBackspace) {
            if (blockDeleteCommHelper.isContentCommentedByOthers(startOffset, endOffset)) {
              notifyUserThatContentCannotBeRemoved(documentModel);
              return false;
            }
            
            return super.delete(filterBypass, startOffset, endOffset, withBackspace);
          }
          
          @Override
          public boolean deleteNode(AuthorDocumentFilterBypass filterBypass, AuthorNode node) {
            if (blockDeleteCommHelper.isContentCommentedByOthers(node.getStartOffset(), node.getEndOffset())) {
              notifyUserThatContentCannotBeRemoved(documentModel);
              return false;
            }
            
            return super.deleteNode(filterBypass, node);
          }
          
          @Override
          public void multipleDelete(AuthorDocumentFilterBypass filterBypass, AuthorElement parentElement, int[] startOffsets, int[] endOffsets) {
            if (blockDeleteCommHelper.isContentCommentedByOthers(startOffsets, endOffsets)) {
              notifyUserThatContentCannotBeRemoved(documentModel);
              return;
            }
            
            super.multipleDelete(filterBypass, parentElement, startOffsets, endOffsets);
          }
          
          private void notifyUserThatContentCannotBeRemoved(AuthorDocumentModel documentModel) {
            documentModel.getAuthorAccess().getWorkspaceAccess().showWarningMessage("Cannot delete content because it contains another user's comments.\n"
                + "You can insert a comment to suggest this change.");
          }
        });
      }
    });
  }
  
  @Override
  public boolean applicationClosing() {
    return true;
  }

}
