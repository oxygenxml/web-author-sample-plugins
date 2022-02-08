package com.oxygenxml;

import ro.sync.ecss.extensions.api.AuthorDocumentFilter;
import ro.sync.ecss.extensions.api.AuthorDocumentFilterBypass;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Custom extension that adds a highlight over each paragraph from the document
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {
  
  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace workspace = (WebappPluginWorkspace) pluginWorkspaceAccess;
    
    workspace.addEditingSessionLifecycleListener(new WebappEditingSessionLifecycleListener() {
      
      @Override
      public void editingSessionStarted(String sessionId,
          AuthorDocumentModel documentModel) {
        ParagraphsHighlighter highlighter = new ParagraphsHighlighter(documentModel.getAuthorAccess());
        
        /**
         * Recompute the highlights on each edit in the document
         */
        documentModel.getAuthorDocumentController().setDocumentFilter(new AuthorDocumentFilter() {
          @Override
          public boolean insertNode(AuthorDocumentFilterBypass filterBypass, int offset, AuthorNode node) {
            highlighter.recomputeHighlights();
            return filterBypass.insertNode(offset, node);
          }
          
          @Override
          public void insertFragment(AuthorDocumentFilterBypass filterBypass,
              int offset, AuthorDocumentFragment frag) {
            highlighter.recomputeHighlights();
            filterBypass.insertFragment(offset, frag);
          }
          
          @Override
          public void insertText(AuthorDocumentFilterBypass filterBypass,
              int offset, String toInsert) {
            highlighter.recomputeHighlights();
            super.insertText(filterBypass, offset, toInsert);
          }
          
          @Override
          public void insertMultipleElements(
              AuthorDocumentFilterBypass filterBypass,
              AuthorElement parentElement, String[] elementNames, int[] offsets,
              String namespace) {
            highlighter.recomputeHighlights();
            super.insertMultipleElements(filterBypass, parentElement, elementNames, offsets,
                namespace);
          }
          
          @Override
          public boolean insertMultipleFragments(
              AuthorDocumentFilterBypass filterBypass,
              AuthorElement parentElement, AuthorDocumentFragment[] fragments,
              int[] offsets) {
            highlighter.recomputeHighlights();
            return super.insertMultipleFragments(filterBypass, parentElement, fragments,
                offsets);
          }
          
          @Override
          public boolean delete(AuthorDocumentFilterBypass filterBypass,
              int startOffset, int endOffset, boolean withBackspace) {
            highlighter.recomputeHighlights();
            return super.delete(filterBypass, startOffset, endOffset, withBackspace);
          }
          
          @Override
          public boolean deleteNode(AuthorDocumentFilterBypass filterBypass,
              AuthorNode node) {
            highlighter.recomputeHighlights();
            return super.deleteNode(filterBypass, node);
          }
          
          @Override
          public void renameElement(AuthorDocumentFilterBypass filterBypass,
              AuthorElement element, String newName, Object infoProvider) {
            highlighter.recomputeHighlights();
            super.renameElement(filterBypass, element, newName, infoProvider);
          }
          
          @Override
          public void surroundInText(AuthorDocumentFilterBypass filterBypass,
              String header, String footer, int startOffset, int endOffset)
              throws AuthorOperationException {
            highlighter.recomputeHighlights();
            super.surroundInText(filterBypass, header, footer, startOffset, endOffset);
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
