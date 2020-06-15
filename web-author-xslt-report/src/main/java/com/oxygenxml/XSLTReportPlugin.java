package com.oxygenxml;

import java.io.File;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

/**
 * Sample extension for <code>ro.sync.exml.plugin.Plugin</code>.
 */
public class XSLTReportPlugin extends Plugin {

  /**
   * Plugin's base directory.
   */
  static File baseDir;

  /**
   * Constructor.
   * @param descriptor Plug-in descriptor.
   */
  public XSLTReportPlugin(PluginDescriptor descriptor) {
    super(descriptor);
    baseDir = descriptor.getBaseDir(); // NOSONAR
  }
}
