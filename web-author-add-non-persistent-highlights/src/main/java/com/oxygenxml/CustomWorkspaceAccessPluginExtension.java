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
            boolean insertNode =  filterBypass.insertNode(offset, node);
            highlighter.recomputeHighlights();
            return insertNode;
          }
          
          @Override
          public void insertFragment(AuthorDocumentFilterBypass filterBypass,
              int offset, AuthorDocumentFragment frag) {
            filterBypass.insertFragment(offset, frag);
            highlighter.recomputeHighlights();
          }
          
          @Override
          public void insertText(AuthorDocumentFilterBypass filterBypass,
              int offset, String toInsert) {
            super.insertText(filterBypass, offset, toInsert);
            highlighter.recomputeHighlights();
          }
          
          @Override
          public void insertMultipleElements(
              AuthorDocumentFilterBypass filterBypass,
              AuthorElement parentElement, String[] elementNames, int[] offsets,
              String namespace) {
            super.insertMultipleElements(filterBypass, parentElement, elementNames, offsets,
                namespace);
            highlighter.recomputeHighlights();
          }
          
          @Override
          public boolean insertMultipleFragments(
              AuthorDocumentFilterBypass filterBypass,
              AuthorElement parentElement, AuthorDocumentFragment[] fragments,
              int[] offsets) {
            boolean insert = super.insertMultipleFragments(filterBypass, parentElement, fragments,
                offsets);
            highlighter.recomputeHighlights();
            return insert;
          }
          
          @Override
          public boolean delete(AuthorDocumentFilterBypass filterBypass,
              int startOffset, int endOffset, boolean withBackspace) {
            boolean delete = super.delete(filterBypass, startOffset, endOffset, withBackspace);
            highlighter.recomputeHighlights();
            return delete;
          }
          
          @Override
          public boolean deleteNode(AuthorDocumentFilterBypass filterBypass,
              AuthorNode node) {
            boolean delete = super.deleteNode(filterBypass, node);
            highlighter.recomputeHighlights();
            return delete;
          }
          
          @Override
          public void renameElement(AuthorDocumentFilterBypass filterBypass,
              AuthorElement element, String newName, Object infoProvider) {
            super.renameElement(filterBypass, element, newName, infoProvider);
            highlighter.recomputeHighlights();
          }
          
          @Override
          public void surroundInText(AuthorDocumentFilterBypass filterBypass,
              String header, String footer, int startOffset, int endOffset)
              throws AuthorOperationException {
            super.surroundInText(filterBypass, header, footer, startOffset, endOffset);
            highlighter.recomputeHighlights();
          }
          
          @Override
          public void multipleDelete(AuthorDocumentFilterBypass filterBypass,
              AuthorElement parentElement, int[] startOffsets,
              int[] endOffsets) {
            super.multipleDelete(filterBypass, parentElement, startOffsets, endOffsets);
            highlighter.recomputeHighlights();
          }
          
          @Override
          public boolean split(AuthorDocumentFilterBypass filterBypass,
              AuthorNode toSplit, int splitOffset) {
            boolean split = super.split(filterBypass, toSplit, splitOffset);
            highlighter.recomputeHighlights();
            return split;
          }
          
          @Override
          public void surroundInFragment(
              AuthorDocumentFilterBypass filterBypass,
              AuthorDocumentFragment xmlFragment, int startOffset,
              int endOffset) throws AuthorOperationException {
            highlighter.recomputeHighlights();
            super.surroundInFragment(filterBypass, xmlFragment, startOffset, endOffset);
          }
          
          @Override
          public void surroundInFragment(
              AuthorDocumentFilterBypass filterBypass, String xmlFragment,
              int startOffset, int endOffset) throws AuthorOperationException {
            super.surroundInFragment(filterBypass, xmlFragment, startOffset, endOffset);
            highlighter.recomputeHighlights();
          }
          
          @Override
          public void surroundWithNode(AuthorDocumentFilterBypass filterBypass,
              AuthorNode node, int startOffset, int endOffset,
              boolean leftToRight) {
            super.surroundWithNode(filterBypass, node, startOffset, endOffset, leftToRight);
            highlighter.recomputeHighlights();
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
