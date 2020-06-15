package com.oxygenxml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import ro.sync.basic.util.URLUtil;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

@WebappCompatible
@WebappRestSafe
public class XSLTReportOperation extends AuthorOperationWithResult {

  
  @Override
  public String doOperation(AuthorDocumentModel model, ArgumentsMap args)
      throws AuthorOperationException {
    
    AuthorAccess authorAccess = model.getAuthorAccess();
    
    Source xslSrc = new SAXSource(new org.xml.sax.InputSource(getScriptLocation(args)));
    Transformer transformer;
    try {
      transformer = authorAccess.getXMLUtilAccess().createXSLTTransformer(xslSrc, new URL[0], 
          XMLUtilAccess.TRANSFORMER_SAXON_PROFESSIONAL_EDITION);
    } catch (TransformerConfigurationException e) {
      throw new IllegalStateException(e);
    }
    
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    Reader docReader = editorAccess.createContentReader();
    org.xml.sax.InputSource is = new org.xml.sax.InputSource(docReader);
    is.setSystemId(editorAccess.getEditorLocation().toExternalForm());
    Source xmlSrc = new SAXSource(is);
    StringWriter sw = new StringWriter();
    
    try {
      transformer.transform(xmlSrc, new StreamResult(sw ));
    } catch (TransformerException e) {
      return "FAILURE";
    }
    
    return sw.toString();
  }

  private String getScriptLocation(ArgumentsMap args) {
    String scriptLocation = (String) args.getArgumentValue("script");
    File script = new File(XSLTReportPlugin.baseDir, scriptLocation);
    try {
      if (contains(XSLTReportPlugin.baseDir, script)) {
        return URLUtil.correct(script).toExternalForm();
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("The 'script' file is not located in the plugin's folder.", e);
    }
    return null;
  }

  private boolean contains(File folder, File file) throws IOException {
    return file.getCanonicalPath().startsWith(folder.getCanonicalPath() + File.separator);
  }

}
