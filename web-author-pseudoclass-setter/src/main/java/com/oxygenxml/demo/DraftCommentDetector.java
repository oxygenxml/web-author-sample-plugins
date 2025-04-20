package com.oxygenxml.demo;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorExtensionStateListener;
import ro.sync.ecss.extensions.api.AuthorListenerAdapter;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.AuthorPreloadProcessor;
import ro.sync.ecss.extensions.api.AuthorPseudoClassController;
import ro.sync.ecss.extensions.api.DocumentContentDeletedEvent;
import ro.sync.ecss.extensions.api.DocumentContentInsertedEvent;

/**
 * Listener to detect draft comments and update the pseudo-class.
 */
public class DraftCommentDetector extends AuthorListenerAdapter implements AuthorExtensionStateListener, AuthorPreloadProcessor {
    /**
     * The pseudo-class to set on the root element when draft comments are present.
     */
    private static final String CONTAINS_DRAFT_COMMENT_PSEUDO_CLASS = "-oxy-contains-draft-comment";
    
    private AuthorDocumentController controller;


    @Override
    public void activated(AuthorAccess authorAccess) {
        this.controller = authorAccess.getDocumentController();
        if (this.controller != null) {
            this.controller.addAuthorListener(this);
            updateRootPseudoClass();
        }
    }

    @Override
    public void deactivated(AuthorAccess authorAccess) {
        if (this.controller != null) {
            this.controller.removeAuthorListener(this);
            // Clean up the pseudo-class when deactivated
            removeRootPseudoClass();
        }
        this.controller = null;
    }

    @Override
    public void documentChanged(AuthorDocument oldDocument, AuthorDocument newDocument) {
        updateRootPseudoClass();
    }

    @Override
    public void contentDeleted(DocumentContentDeletedEvent e) {
        if (e.getType() == DocumentContentDeletedEvent.DELETE_FRAGMENT_EVENT && 
                hasDraftComments(e.getDeletedFragment())) {
            updateRootPseudoClass();
        }
    }

    @Override
    public void contentInserted(DocumentContentInsertedEvent e) {
        if (e.getType() == DocumentContentInsertedEvent.INSERT_FRAGMENT_EVENT && 
                hasDraftComments(e.getInsertedFragment())) {
            updateRootPseudoClass();
        }
    }

    @Override
    public void authorNodeStructureChanged(AuthorNode node) {
        updateRootPseudoClass();
    }
    
    @Override
    public void doctypeChanged() {
        updateRootPseudoClass();
    }

    @Override
    public void authorNodeNameChanged(AuthorNode node) {
        updateRootPseudoClass();
    }
    
    @Override
    public void documentAboutToBeLoaded(AuthorDocument document, AuthorPseudoClassController pseudoClassController) {
        AuthorElement rootElement = document.getRootElement();
        if (rootElement == null) {
            return;
        }
        if (hasDraftComments(rootElement)) {
            pseudoClassController.setPseudoClass(CONTAINS_DRAFT_COMMENT_PSEUDO_CLASS, rootElement);
        } else {
            pseudoClassController.removePseudoClass(CONTAINS_DRAFT_COMMENT_PSEUDO_CLASS, rootElement);
        }
    }

    /**
     * Recursively checks if the node or any of its children has a 'draft-comment' element.
     * 
     * @param node The node to check
     * @return True if the node or any of its children has a 'draft-comment' element, false otherwise
     */
    private boolean hasDraftComments(AuthorNode node) {
        if (node == null) {
            return false;
        }
        if (node.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
            AuthorElement element = (AuthorElement) node;
            if (element.getLocalName().equals("draft-comment")) {
                return true;
            }
            for (AuthorNode child : element.getContentNodes()) { 
                if (hasDraftComments(child)) {
                    return true;
                }
            }
        }
       
        return false;
    }
    
    private boolean hasDraftComments(AuthorDocumentFragment fragment) {
        if (fragment == null) {
            return false;
        }
        for (AuthorNode node : fragment.getContentNodes()) {
            if (hasDraftComments(node)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the document contains any 'draft-comment' elements and updates the
     * pseudo-class on the root element accordingly.
     */
    private void updateRootPseudoClass() {
        if (controller == null) {
            return;
        }
        AuthorElement rootElement = controller.getAuthorDocumentNode().getRootElement();
        if (rootElement == null) {
            return;
        }

        if (hasDraftComments(rootElement)) {
            controller.setPseudoClass(CONTAINS_DRAFT_COMMENT_PSEUDO_CLASS, rootElement);
        } else {
            controller.removePseudoClass(CONTAINS_DRAFT_COMMENT_PSEUDO_CLASS, rootElement);
        }
    }
    
    /**
     * Removes the pseudo-class from the root element.
     */
    private void removeRootPseudoClass() {
        if (controller != null) {
            AuthorElement rootElement = controller.getAuthorDocumentNode().getRootElement();
            if (rootElement != null) {
                controller.removePseudoClass(CONTAINS_DRAFT_COMMENT_PSEUDO_CLASS, rootElement);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Detects draft comments and sets a pseudo-class on the root element.";
    }
} 