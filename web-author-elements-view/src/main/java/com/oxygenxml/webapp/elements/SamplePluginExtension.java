package com.oxygenxml.webapp.elements;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

/**
 * Sample extension for <code>ro.sync.exml.plugin.Plugin</code>.
 */
public class SamplePluginExtension extends Plugin {

  /**
   * Constructor.
   * @param descriptor Plug-in descriptor.
   */
  public SamplePluginExtension(PluginDescriptor descriptor) {
    super(descriptor);
  }
}
