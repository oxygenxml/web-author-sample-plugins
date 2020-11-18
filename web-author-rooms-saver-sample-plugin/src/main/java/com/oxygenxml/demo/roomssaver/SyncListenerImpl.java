package com.oxygenxml.demo.roomssaver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ro.sync.ecss.extensions.api.access.UnsavedContentReferenceManager;
import ro.sync.ecss.extensions.api.webapp.ce.PeerContext;
import ro.sync.ecss.extensions.api.webapp.ce.Room;
import ro.sync.ecss.extensions.api.webapp.ce.RoomObserver;
import ro.sync.ecss.extensions.api.webapp.ce.RoomObserver.SyncListener;

/**
 * Listener used when synchronize the Observer's
 * state with the latest changes in the room.
 */
class SyncListenerImpl implements SyncListener {

  /**
   * Logger.
   */
  private static final Logger logger = LogManager.getLogger(SyncListenerImpl.class);
  
  /**
   * The document URL.
   */
  private URL docUrl;
  /**
   * The room.
   */
  private Room room;

  /**
   * Constructor.
   * @param docUrl Document URL.
   * @param room The room.
   */
  SyncListenerImpl(URL docUrl, Room room) {
    this.docUrl = docUrl;
    this.room = room;
  }

  @Override
  public void changesSynchronized(int peerId) {
    try {
      RoomObserver observer = room.getObserver();
      PeerContext peerContext = room.getPeerContext(peerId);

      InputStream inputStream = observer.createInputStream();
      CmsSaver.save(inputStream, docUrl, peerContext);

      UnsavedContentReferenceManager unsavedReferencesManager =
          observer.getUnsavedContentReferenceManager();

      if (unsavedReferencesManager != null) {
        List<URL> unsavedReferences = unsavedReferencesManager.getUnsavedReferencesList();
        for (URL referenceUrl : unsavedReferences) {
          InputStream referenceInputStream =
              unsavedReferencesManager.getUnsavedReferenceInputStream(referenceUrl);
          CmsSaver.save(referenceInputStream, referenceUrl, peerContext);
        }
      }
    } catch (IOException e) {
      logger.warn(e, e);
    }
  }
}
