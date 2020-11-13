package com.oxygenxml.demo.roomsmanager;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ro.sync.basic.util.URLUtil;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.EditingSessionOpenVetoException;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.ce.Room;
import ro.sync.ecss.extensions.api.webapp.ce.RoomsManager;

/**
 * Session lifecycle listener that assign each document to a room.
 * If two users open the same document, they will be in the same concurrent session.
 */
class EditingSessionLifecycleListener extends WebappEditingSessionLifecycleListener{

  /**
   * Logger for logging.
   */
  private static final Logger logger = 
      LogManager.getLogger(EditingSessionLifecycleListener.class.getName());

  /**
   * Store room IDs.
   */
  private RoomIdsStore roomIdsStore = new RoomIdsStore(); 

  /**
   * Map from document URL to its lock.
   */
  private Map<URL, Lock> mapSystemIdToLock = new HashMap<>();

  /**
   * Map from room ID its peers counter.
   */
  private Map<String, AtomicInteger> mapRoomIdToPeersCounter = new HashMap<>();

  @Override
  public void editingSessionAboutToBeStarted(
      String sessionId, String licenseeId, URL systemId, Map<String, Object> options)
          throws EditingSessionOpenVetoException {
    super.editingSessionAboutToBeStarted(sessionId, licenseeId, systemId, options);

    Lock lock = getLock(systemId);
    lock.lock();

    Optional<String> roomId = roomIdsStore.getRoomId(systemId);
    if (roomId.isPresent()) {
      logger.warn("Open {} in already existing room {}", systemId.getPath(), roomId.get());
      options.put(Room.ROOM_ID_ATTRIBUTE, roomId.get());
    }
  }

  @Override
  public void editingSessionStarted(String sessionId, AuthorDocumentModel documentModel) {
    super.editingSessionStarted(sessionId, documentModel);

    URL systemId = getDocUrl(documentModel);
    Optional<String> roomId = roomIdsStore.getRoomId(systemId);
    if (roomId.isPresent()) {
      getPeersCounter(roomId.get()).incrementAndGet();
    } else {
      String newRoomId = RoomsManager.INSTANCE.createRoomFromDocument(documentModel);
      logger.warn("Create room {} from first opened document {}", newRoomId, systemId.getPath());
      roomIdsStore.setRoomId(systemId, newRoomId);

      getPeersCounter(newRoomId).incrementAndGet();
    }

    Lock lock = getLock(systemId);
    lock.unlock();
  }

  @Override
  public void editingSessionFailedToStart(
      String sessionId, String licenseeId, URL systemId, Map<String, Object> options) {
    super.editingSessionFailedToStart(sessionId, licenseeId, systemId, options);

    logger.warn("Failed to open document {}", systemId.getPath());
    Lock lock = getLock(systemId);
    lock.unlock();
  }

  @Override
  public void editingSessionClosed(String sessionId, AuthorDocumentModel documentModel) {
    super.editingSessionClosed(sessionId, documentModel);

    Lock lock = getLock(getDocUrl(documentModel));
    lock.lock();
    try {
      URL systemId = getDocUrl(documentModel);
      String roomId = roomIdsStore.getRoomId(systemId)
          .orElseThrow(IllegalStateException::new);

      // Close the room when it become empty.
      AtomicInteger peersCounter = getPeersCounter(roomId);
      if(peersCounter.decrementAndGet() == 0) {
        logger.warn("Close empty room {}", roomId);
        Room room = RoomsManager.INSTANCE.getRoom(roomId)
            .orElseThrow(IllegalStateException::new);
        room.close();

        roomIdsStore.setRoomId(systemId, null);
        mapSystemIdToLock.remove(systemId);
        mapRoomIdToPeersCounter.remove(roomId);
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * @param documentModel Author model
   * @return Document URL.
   */
  private URL getDocUrl(AuthorDocumentModel documentModel) {
    return documentModel.getWSEditor().getEditorLocation();
  }

  /**
   * @param docUrl The document URL.
   * @return the lock corresponding to the given URL.
   */
  private Lock getLock(URL docUrl) {
    docUrl = URLUtil.clearUserInfo(docUrl);
    mapSystemIdToLock.computeIfAbsent(docUrl, (d) -> new ReentrantLock());
    return mapSystemIdToLock.get(docUrl);
  }

  /**
   * @param roomId The room Id.
   * @return the peers counter.
   */
  private AtomicInteger getPeersCounter(String roomId) {
    mapRoomIdToPeersCounter.computeIfAbsent(roomId, (d) -> new AtomicInteger());
    return mapRoomIdToPeersCounter.get(roomId);
  }
}
