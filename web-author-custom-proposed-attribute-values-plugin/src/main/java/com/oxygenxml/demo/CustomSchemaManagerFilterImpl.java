package com.oxygenxml.demo;

import java.util.Arrays;
import java.util.List;

import ro.sync.contentcompletion.xml.CIAttribute;
import ro.sync.contentcompletion.xml.CIElement;
import ro.sync.contentcompletion.xml.CIValue;
import ro.sync.contentcompletion.xml.Context;
import ro.sync.contentcompletion.xml.SchemaManagerFilter;
import ro.sync.contentcompletion.xml.WhatAttributesCanGoHereContext;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.contentcompletion.xml.WhatPossibleValuesHasAttributeContext;

class CustomSchemaManagerFilterImpl implements SchemaManagerFilter {

  private SchemaManagerFilter wrappedFilter;

  CustomSchemaManagerFilterImpl(SchemaManagerFilter wrappedFilter) {
    this.wrappedFilter = wrappedFilter;
  }

  public String getDescription() {
    return "Extension that sets \"Oxygen XML Editor\" and \"Oxygen XML Author\" "
        + "as the proposed values for the \"product\" attribute";
  }

  public List<CIValue> filterAttributeValues(
      List<CIValue> attributeValues,
      WhatPossibleValuesHasAttributeContext context) {
    if ("product".equals(context.getAttributeName())) {
      System.out.println("CustomSchemaManagerFilterImpl.filterAttributeValues() - set custom attribute values.");
      return Arrays.asList(
          new CIValue("Oxygen XML Editor"),
          new CIValue("Oxygen XML Web Author"));
    } else {
      System.out.println("CustomSchemaManagerFilterImpl.filterAttributeValues() - delegate to default filter.");
      return wrappedFilter.filterAttributeValues(attributeValues, context);
    }
  }

  public List<CIValue> filterElementValues(
      List<CIValue> elementValues,
      Context context) {
    return wrappedFilter.filterElementValues(elementValues, context);
  }

  public List<CIAttribute> filterAttributes(
      List<CIAttribute> attributes,
      WhatAttributesCanGoHereContext context) {
    return wrappedFilter.filterAttributes(attributes, context);
  }

  public List<CIElement> filterElements(
      List<CIElement> elements,
      WhatElementsCanGoHereContext context) {
    return wrappedFilter.filterElements(elements, context);
  }
}
