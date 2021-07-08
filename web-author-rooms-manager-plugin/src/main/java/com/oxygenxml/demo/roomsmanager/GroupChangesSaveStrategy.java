package com.oxygenxml.demo.roomsmanager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import ro.sync.ecss.extensions.api.webapp.ce.GroupChangesForMultiplePeersStrategy;
import ro.sync.ecss.extensions.api.webapp.ce.PeerContext;

/**
 * Basic implementation of GroupChangesForMultiplePeersStrategy that
 * saves the changes made by peers on behalf of committer (the one requests to save).
 */
class GroupChangesSaveStrategy implements GroupChangesForMultiplePeersStrategy {
  @Override
  public URLConnection openConnection(
      URL documentUrl, PeerContext committerContext, List<PeerContext> authorsContext)
      throws IOException {
    return documentUrl.openConnection();
  }
}
