package com.oxygenxml.web_author.react;

import java.io.IOException;
import java.io.Writer;

import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.webapp.formcontrols.WebappFormControlRenderer;

public class StepperRenderer extends WebappFormControlRenderer {

  @Override
  public String getDescription() {
    return "";
  }
  
  @Override
  public void renderControl(AuthorInplaceContext context, Writer writer) throws IOException {
    writer.append("<span></span>");
  }

}
