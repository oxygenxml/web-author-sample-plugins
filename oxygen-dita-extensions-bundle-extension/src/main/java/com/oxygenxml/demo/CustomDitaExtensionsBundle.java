package com.oxygenxml.demo;

import java.io.IOException;
import java.net.URL;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.dita.DITAExtensionsBundle;

/**
 * Custom DITAExtensionsBundle.
 */
public class CustomDitaExtensionsBundle extends DITAExtensionsBundle {
  @Override
  public URL resolveCustomHref(URL currentEditorURL, String linkHref, AuthorAccess authorAccess) throws IOException {
    System.out.println("resolveCustomHref: " + currentEditorURL);
    return super.resolveCustomHref(currentEditorURL, linkHref, authorAccess);
  }
}
