package com.oxygenxml;

import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.plugin.author.css.filter.GeneralStylesFilterExtension;

/**
 * Custom styles filter that can be used to replace the CSS styles associated 
 * to an element or a pseudo element.
 */
public class CustomStylesFilter extends GeneralStylesFilterExtension {

  public String getDescription() {
    return "Custom Styles Filter";
  }

  public Styles filter(Styles styles, AuthorNode authorNode) {
    // Change the styles of the Author node here
    return styles;
  }

}
