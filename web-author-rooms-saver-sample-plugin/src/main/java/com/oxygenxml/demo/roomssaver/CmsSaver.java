package com.oxygenxml.demo.roomssaver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ro.sync.ecss.extensions.api.webapp.ce.PeerContext;
import ro.sync.exml.options.Options;

/**
 * Handle saving to the CMS.
 * The implementation is pretty basic,
 * it pushes document's content through HTTPS with credentials stored in options. 
 */
public class CmsSaver {
  /**
   * Logger.
   */
  private static final Logger logger = LogManager.getLogger(CmsSaver.class);

  /**
   * Private constructor.
   */
  private CmsSaver() {}
 
  /**
   * Save the document to the CMS, bypassing the custom URLStreamHandler.
   *
   * @param inputStream The input stream of the edited document.
   * @param documentUrl The URL of the edited document.
   * @param peerContext Details about user who changed it.
   *
   * @throws IOException
   */
  static void save(InputStream inputStream, URL documentUrl, PeerContext peerContext)
      throws IOException {
    Options options = Options.getInstance();

    String protocol = "https";
    String user = options.getStringProperty("cms-user");
    String password = options.getStringProperty("cms-password");
    
    if (user == null || password == null) {
      throw new IllegalStateException("Options 'cms-user' and 'cms-password'"
          + "to be used to save concurrently edited document aren't defined.");
    }

    URL toSaveUrl = set(documentUrl, protocol, user, password);
    URLConnection openConnection = toSaveUrl.openConnection();
    openConnection.setDoOutput(true);

    OutputStream outputStream = openConnection.getOutputStream();
    try {
      IOUtils.copy(inputStream, outputStream);
    } finally {
      try {
        outputStream.close();
      } catch (IOException e) {
        logger.debug(e, e);
      }
    }
  }

  /**
   * Set the protocol, user and password to the given URL.
   *
   * @param url The URL.
   * @param protocol The protocol
   * @param user The user.
   * @param password The password.
   * 
   * @return New URL instance.
   * 
   * @throws MalformedURLException If protocol, user or password are invalid.
   */
  private static URL set(URL url, String protocol, String user, String password)
      throws MalformedURLException {
    StringBuilder sb = new StringBuilder();

    sb.append(protocol)
    .append("://")
    .append(user)
    .append(":")
    .append(password)
    .append("@").append(url.getHost());

    if (url.getPort() != -1) {
      sb.append(":").append(url.getPort());
    }

    sb.append(url.getFile());

    if (url.getRef() != null) {
      sb.append("#").append(url.getRef());
    }

    return new URL(sb.toString());
  }
}
