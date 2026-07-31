package com.oxygenxml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.access.AuthorUtilAccess;
import ro.sync.ecss.extensions.api.access.AuthorXMLUtilAccess;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.validation.ValidationProblems;
import ro.sync.exml.workspace.api.editor.validation.ValidationProblemsFilter;

/**
 * Sample filter that walks the Author document tree and reports, as validation
 * errors, the local/relative href and conref targets that cannot be resolved.
 *
 * Validation runs on (nearly) every edit, so this filter avoids re-checking a
 * link on every keystroke:
 * - For links that resolve to a local file, only a cheap File#exists() check
 *   is done (no stream is opened).
 * - Otherwise (non-local resources, e.g. served through Web Author's own
 *   protocols), the result of the reachability check is cached per resolved
 *   URL on the filter instance, for as long as the instance lives - i.e. the
 *   whole editing session / AuthorDocumentModel lifetime (see
 *   CustomWorkspaceAccessPluginExtension). Editing unrelated content never
 *   re-triggers I/O for a link that was already checked in this session;
 *   only a changed/new href value is a cache miss.
 *
 * Limitations (kept intentionally simple for a sample):
 * - Only the "href" and "conref" attributes are checked.
 * - Absolute http(s)/mailto links are skipped (not checked).
 * - Same-document fragment identifiers (e.g. "#id" or the "#id" part of
 *   "topic.dita#id") are not resolved against the target's IDs, only the
 *   file part is checked for reachability.
 * - keyref is not resolved here (WA already reports unresolved keyref keys
 *   via the built-in DITA reference validation).
 */
public class BrokenLinksValidationProblemsFilter extends ValidationProblemsFilter {

  private static final String[] LINK_ATTRIBUTES = { "href", "conref" };

  private final WSEditor editor;

  private final Map<String, Boolean> reachabilityCache = new ConcurrentHashMap<>();

  public BrokenLinksValidationProblemsFilter(WSEditor editor) {
    this.editor = editor;
  }

  @Override
  public void filterValidationProblems(ValidationProblems validationProblems) {
    try {
      doFilterValidationProblems(validationProblems);
    } catch (Throwable t) {
      // Validation can run concurrently with document edits; walking the live Author tree can
      // then hit a transiently inconsistent state (e.g. a node removed mid-traversal). Nothing in
      // the validation pipeline guards against that for custom filters, and an uncaught exception
      // here would make the caller (AuthorDocumentValidationTask) drop the ENTIRE validation
      // result for this cycle - including the native schema/DITA errors, not just ours. So we
      // just skip the broken-link check for this cycle instead of risking that.
    }
  }

  private void doFilterValidationProblems(ValidationProblems validationProblems) {
    WSEditorPage currentPage = editor.getCurrentPage();
    if (!(currentPage instanceof WSAuthorEditorPage)) {
      // Not an Author (WYSIWYG) page, nothing to check.
      return;
    }

    AuthorAccess authorAccess = ((WSAuthorEditorPage) currentPage).getAuthorAccess();
    AuthorDocument document = authorAccess.getDocumentController().getAuthorDocumentNode();
    AuthorElement root = document.getRootElement();
    if (root == null) {
      return;
    }

    URL baseURL = editor.getEditorLocation();
    AuthorUtilAccess utilAccess = authorAccess.getUtilAccess();
    AuthorXMLUtilAccess xmlUtilAccess = authorAccess.getXMLUtilAccess();
    String systemID = baseURL != null ? baseURL.toString() : null;

    List<DocumentPositionedInfo> problemsList = validationProblems.getProblemsList();
    collectBrokenLinks(root, baseURL, utilAccess, xmlUtilAccess, systemID, problemsList);
  }

  private void collectBrokenLinks(AuthorElement element, URL baseURL, AuthorUtilAccess utilAccess,
      AuthorXMLUtilAccess xmlUtilAccess, String systemID, List<DocumentPositionedInfo> problems) {
    for (String attributeName : LINK_ATTRIBUTES) {
      AttrValue attrValue = element.getAttribute(attributeName);
      String value = attrValue != null ? attrValue.getValue() : null;
      if (value != null && !value.trim().isEmpty()) {
        checkLink(element, attributeName, value.trim(), baseURL, utilAccess, xmlUtilAccess, systemID, problems);
      }
    }

    List<AuthorNode> contentNodes = element.getContentNodes();
    if (contentNodes != null) {
      for (AuthorNode node : contentNodes) {
        if (node instanceof AuthorElement) {
          collectBrokenLinks((AuthorElement) node, baseURL, utilAccess, xmlUtilAccess, systemID, problems);
        }
      }
    }
  }

  private void checkLink(AuthorElement element, String attributeName, String value, URL baseURL,
      AuthorUtilAccess utilAccess, AuthorXMLUtilAccess xmlUtilAccess, String systemID,
      List<DocumentPositionedInfo> problems) {
    if (isSkipped(value)) {
      return;
    }

    // Only resolve/check the file part; same-document target ids are not verified in this sample.
    String filePart = value;
    int hashIndex = value.indexOf('#');
    if (hashIndex == 0) {
      // Pure same-document fragment - nothing external to check.
      return;
    } else if (hashIndex > 0) {
      filePart = value.substring(0, hashIndex);
    }

    URL resolved;
    try {
      resolved = xmlUtilAccess.resolvePathThroughCatalogs(baseURL, filePart, true, true);
      if (resolved == null) {
        resolved = new URL(baseURL, filePart);
      }
    } catch (IOException e) {
      reportBrokenLink(element, attributeName, value, systemID, problems);
      return;
    }

    if (!isReachable(resolved, utilAccess)) {
      reportBrokenLink(element, attributeName, value, systemID, problems);
    }
  }

  private boolean isReachable(URL resolved, AuthorUtilAccess utilAccess) {
    // Local resources: a File#exists() check is enough and far cheaper than opening a stream.
    File localFile = utilAccess.locateFile(resolved);
    if (localFile != null) {
      return localFile.exists();
    }

    String cacheKey = resolved.toString();
    Boolean cached = reachabilityCache.get(cacheKey);
    if (cached != null) {
      return cached;
    }

    boolean reachable = true;
    Reader reader = null;
    try {
      reader = utilAccess.createReader(resolved, "UTF-8");
    } catch (IOException e) {
      reachable = false;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          // Ignore, we already know the resource was reachable enough to open a reader.
        }
      }
    }

    reachabilityCache.put(cacheKey, reachable);
    return reachable;
  }

  private void reportBrokenLink(AuthorElement element, String attributeName, String value, String systemID,
      List<DocumentPositionedInfo> problems) {
    DocumentPositionedInfo problem = new DocumentPositionedInfo(
        DocumentPositionedInfo.SEVERITY_ERROR,
        "Broken link: the " + attributeName + "=\"" + value + "\" target could not be resolved.",
        systemID);
    problem.setOffset(element.getStartOffset());
    problems.add(problem);
  }

  private boolean isSkipped(String value) {
    String lower = value.toLowerCase();
    return lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("mailto:")
        || lower.startsWith("ftp://");
  }

}
