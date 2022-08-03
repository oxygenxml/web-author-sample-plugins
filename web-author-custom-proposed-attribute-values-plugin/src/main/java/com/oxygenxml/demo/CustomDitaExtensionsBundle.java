package com.oxygenxml.demo;

import ro.sync.contentcompletion.xml.SchemaManagerFilter;
import ro.sync.ecss.extensions.dita.DITAExtensionsBundle;

/**
 * Custom DITAExtensionsBundle.
 */
public class CustomDitaExtensionsBundle extends DITAExtensionsBundle {
  @Override
  public SchemaManagerFilter createSchemaManagerFilter() {
    return new CustomSchemaManagerFilterImpl(super.createSchemaManagerFilter());
  }
}
