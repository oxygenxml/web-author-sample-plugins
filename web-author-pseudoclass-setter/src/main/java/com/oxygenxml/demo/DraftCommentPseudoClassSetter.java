package com.oxygenxml.demo;

import ro.sync.ecss.extensions.api.AuthorExtensionStateListener;
import ro.sync.ecss.extensions.api.AuthorExtensionStateListenerDelegator;
import ro.sync.ecss.extensions.api.AuthorPreloadProcessor;
import ro.sync.ecss.extensions.dita.DITAExtensionsBundle;

/**
 * Sets a pseudo-class on the root element if any "draft-comment" elements are found in the document.
 */
public class DraftCommentPseudoClassSetter extends DITAExtensionsBundle {

  /**
   * @see ro.sync.ecss.extensions.dita.DITAExtensionsBundle#createAuthorExtensionStateListener()
   */
  @Override
  public AuthorExtensionStateListener createAuthorExtensionStateListener() {
    AuthorExtensionStateListenerDelegator delegator = new AuthorExtensionStateListenerDelegator();
    delegator.addListener(super.createAuthorExtensionStateListener()); // Keep DITA listeners if needed
    delegator.addListener(new DraftCommentDetector());
    return delegator;
  }

  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createAuthorPreloadProcessor()
   */
  @Override
  public AuthorPreloadProcessor createAuthorPreloadProcessor() {
    PreloadProcessorDelegator delegator = new PreloadProcessorDelegator();
    delegator.addProcessor(super.createAuthorPreloadProcessor());
    delegator.addProcessor(new DraftCommentDetector());
    return delegator;
  }

  @Override
  public String getDescription() {
    return "Sets pseudo-class on root if draft comments exist";
  }
} 