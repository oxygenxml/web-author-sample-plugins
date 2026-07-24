package com.oxygenxml;

import java.util.List;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorDocumentFilter;
import ro.sync.ecss.extensions.api.AuthorDocumentFilterBypass;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Web Author plugin that prevents editing inside DITA topics marked read-only
 * via {@code outputclass="readonly"} on the corresponding topicref, and
 * replaces Oxygen's built-in "Read-only content: Use \"Edit Reference\" action
 * from the contextual menu." message with a custom one.
 * <p>
 * The attribute and the value token that mark a topic read-only are
 * configurable through the {@link #READONLY_ATTR} / {@link #READONLY_VALUE}
 * constants below.
 * <p>
 * Modelled after the {@code web-author-block-deleting-comments} sample: it
 * installs an {@link AuthorDocumentFilter} on every editing session and rejects
 * editing operations whose target lies inside a read-only topic.
 * <p>
 * IMPORTANT: An {@code AuthorDocumentFilter} is only reached when the target
 * content is editable at the core level. If the same topics are ALSO locked
 * with the CSS {@code -oxy-editable:false}, Oxygen rejects the edit (and shows
 * its own message) BEFORE the filter runs, so this plugin never gets a chance
 * to speak. To let this plugin own the message, the read-only enforcement must
 * live here (in the filter) instead of in {@code -oxy-editable:false}.
 */
public class ReadOnlyTopicMessagePluginExtension implements WorkspaceAccessPluginExtension {

  /** Attribute that marks a topicref (and its referenced content) read-only. */
  private static final String READONLY_ATTR = "outputclass";
  /**
   * Attribute value token that identifies a read-only region. Matched as one of
   * the whitespace-separated tokens of {@link #READONLY_ATTR}, so
   * {@code outputclass="readonly"} and {@code outputclass="foo readonly"} both
   * qualify. Change this to your own convention if needed.
   */
  private static final String READONLY_VALUE = "readonly";
  /** The custom message shown to the user. Set to {@code null} for silent reject. */
  private static final String CUSTOM_MESSAGE = "Cannot edit this topic. It is marked as read-only.";

  @Override
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspaceAccess) {
    WebappPluginWorkspace workspace = (WebappPluginWorkspace) pluginWorkspaceAccess;

    WebappEditingSessionLifecycleListener listener = new WebappEditingSessionLifecycleListener() {
      @Override
      public void editingSessionStarted(String sessionId, AuthorDocumentModel documentModel) {
        installFilter(documentModel);
      }
    };

    // Cover both plain topic editing sessions and DITA Map editing sessions
    // (topicref[outputclass] lives in the map; referenced content is edited there).
    workspace.addEditingSessionLifecycleListener(listener);
    workspace.addDITAMapEditingSessionLifecycleListener(listener);
  }

  /**
   * Installs the read-only-enforcing document filter on the given session.
   */
  private void installFilter(final AuthorDocumentModel documentModel) {
    final AuthorDocumentController controller = documentModel.getAuthorDocumentController();

    controller.setDocumentFilter(new AuthorDocumentFilter() {

      @Override
      public void insertText(AuthorDocumentFilterBypass bypass, int offset, String text) {
        if (isReadOnlyAtOffset(controller, offset)) {
          notifyReadOnly(documentModel);
          return;
        }
        super.insertText(bypass, offset, text);
      }

      @Override
      public void insertFragment(AuthorDocumentFilterBypass bypass, int offset,
          ro.sync.ecss.extensions.api.node.AuthorDocumentFragment frag) {
        if (isReadOnlyAtOffset(controller, offset)) {
          notifyReadOnly(documentModel);
          return;
        }
        super.insertFragment(bypass, offset, frag);
      }

      @Override
      public boolean insertNode(AuthorDocumentFilterBypass bypass, int offset, AuthorNode node) {
        if (isReadOnlyAtOffset(controller, offset)) {
          notifyReadOnly(documentModel);
          return false;
        }
        return super.insertNode(bypass, offset, node);
      }

      @Override
      public boolean delete(AuthorDocumentFilterBypass bypass, int startOffset, int endOffset,
          boolean withBackspace) {
        if (isReadOnlyAtOffset(controller, startOffset) || isReadOnlyAtOffset(controller, endOffset)) {
          notifyReadOnly(documentModel);
          return false;
        }
        return super.delete(bypass, startOffset, endOffset, withBackspace);
      }

      @Override
      public boolean deleteNode(AuthorDocumentFilterBypass bypass, AuthorNode node) {
        if (isReadOnlyNode(node)) {
          notifyReadOnly(documentModel);
          return false;
        }
        return super.deleteNode(bypass, node);
      }

      @Override
      public void multipleDelete(AuthorDocumentFilterBypass bypass, AuthorElement parentElement,
          int[] startOffsets, int[] endOffsets) {
        if (isReadOnlyNode(parentElement)) {
          notifyReadOnly(documentModel);
          return;
        }
        for (int i = 0; i < startOffsets.length; i++) {
          if (isReadOnlyAtOffset(controller, startOffsets[i])) {
            notifyReadOnly(documentModel);
            return;
          }
        }
        super.multipleDelete(bypass, parentElement, startOffsets, endOffsets);
      }

      @Override
      public boolean split(AuthorDocumentFilterBypass bypass, AuthorNode node, int offset) {
        if (isReadOnlyAtOffset(controller, offset) || isReadOnlyNode(node)) {
          notifyReadOnly(documentModel);
          return false;
        }
        return super.split(bypass, node, offset);
      }

      @Override
      public void setAttribute(AuthorDocumentFilterBypass bypass, String attribute,
          AttrValue value, AuthorElement element) {
        if (isReadOnlyNode(element)) {
          notifyReadOnly(documentModel);
          return;
        }
        super.setAttribute(bypass, attribute, value, element);
      }

      @Override
      public void removeAttribute(AuthorDocumentFilterBypass bypass, String attribute,
          AuthorElement element) {
        if (isReadOnlyNode(element)) {
          notifyReadOnly(documentModel);
          return;
        }
        super.removeAttribute(bypass, attribute, element);
      }
    });
  }

  /**
   * @return {@code true} if the node at the given offset is inside a read-only region.
   */
  private static boolean isReadOnlyAtOffset(AuthorDocumentController controller, int offset) {
    try {
      return isReadOnlyNode(controller.getNodeAtOffset(offset));
    } catch (Exception e) {
      // If we cannot resolve the node, do not block.
      return false;
    }
  }

  /**
   * Walks up the ancestor chain looking for an element whose
   * {@link #READONLY_ATTR} contains the {@link #READONLY_VALUE} token
   * (matches a whitespace-separated token too).
   */
  private static boolean isReadOnlyNode(AuthorNode node) {
    AuthorNode current = node;
    while (current != null) {
      if (current instanceof AuthorElement) {
        AttrValue attr = ((AuthorElement) current).getAttribute(READONLY_ATTR);
        if (attr != null && hasToken(attr.getValue(), READONLY_VALUE)) {
          return true;
        }
      }
      current = current.getParent();
    }
    return false;
  }

  /** @return {@code true} if {@code value} contains {@code token} as a whitespace-separated token. */
  private static boolean hasToken(String value, String token) {
    if (value == null) {
      return false;
    }
    for (String part : value.trim().split("\\s+")) {
      if (part.equals(token)) {
        return true;
      }
    }
    return false;
  }

  /** Shows the custom read-only message (or nothing, when {@link #CUSTOM_MESSAGE} is null). */
  private static void notifyReadOnly(AuthorDocumentModel documentModel) {
    if (CUSTOM_MESSAGE != null) {
      documentModel.getAuthorAccess().getWorkspaceAccess().showWarningMessage(CUSTOM_MESSAGE);
    }
  }

  @Override
  public boolean applicationClosing() {
    return true;
  }
}
