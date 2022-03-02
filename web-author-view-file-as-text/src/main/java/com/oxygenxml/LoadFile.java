package com.oxygenxml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.ecss.extensions.api.webapp.plugin.URLStreamHandlerWithContextUtil;

@WebappCompatible
@WebappRestSafe
public class LoadFile extends AuthorOperationWithResult {
  
  URLStreamHandlerWithContextUtil contextUtil = URLStreamHandlerWithContextUtil.getInstance();
  
  @Override
  public String doOperation(AuthorDocumentModel model, ArgumentsMap args) throws AuthorOperationException {
    try {
      String fileUrlString = (String) args.getArgumentValue("url");
      URL fileUrl = buildAuthenticatedFileUrl(model, fileUrlString);
      return readUrlToUtf8String(fileUrl);
    } catch (IOException e) {
      return null;
    }
  }

  private URL buildAuthenticatedFileUrl(AuthorDocumentModel model, String fileUrlString) throws MalformedURLException {
    URL mainDocumentLocation = model.getAuthorAccess().getEditorAccess().getEditorLocation();
    URL fileUrl = new URL(fileUrlString);
    contextUtil.copyContextId(mainDocumentLocation, fileUrl);
    return fileUrl;
  }

  private String readUrlToUtf8String(URL fileUrl) throws IOException, MalformedURLException {
    URLConnection connection = fileUrl.openConnection();
    try (
        InputStream inputStream = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr)) {
      return reader
            .lines()
            .collect(Collectors.joining("\n"));
    }
  }
}
