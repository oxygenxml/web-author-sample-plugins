package com.oxygenxml.demo.roomsmanager;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.basic.util.URLUtil;
import ro.sync.ecss.extensions.api.access.EditingSessionContext;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.EditingSessionOpenVetoException;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.ce.Room;
import ro.sync.ecss.extensions.api.webapp.ce.RoomsManager;

/**
 * Assign each document to a room.
 * If two users open the same document, they will be assigned to the same room,
 * being able to concurrently edit the document.
 */
class DocumentRoomAllocator extends WebappEditingSessionLifecycleListener{

  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(DocumentRoomAllocator.class);

  /**
   * Store room IDs.
   */
  private RoomIdsStore roomIdsStore = new RoomIdsStore(); 

  /**
   * Map from document URL to its lock.
   */
  private Map<URL, Lock> mapSystemIdToLock = new ConcurrentHashMap<>();

  /**
   * Map from room ID its peers counter.
   */
  private Map<String, AtomicInteger> mapRoomIdToPeersCounter = new HashMap<>();

  @Override
  public void editingSessionAboutToBeStarted(
      String sessionId, String licenseeId, URL systemId, Map<String, Object> options)
          throws EditingSessionOpenVetoException {
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
    URL systemId = getDocUrl(documentModel);
    Optional<String> roomId = roomIdsStore.getRoomId(systemId);
    if (roomId.isPresent()) {
      getPeersCounter(roomId.get()).incrementAndGet();
    } else {
      String newRoomId = createAndRecordRoom(documentModel, systemId);
      logger.warn("Create room {} from first opened document {}", newRoomId, systemId.getPath());
      getPeersCounter(newRoomId).incrementAndGet();
    }

    Lock lock = getLock(systemId);
    lock.unlock();
  }

  private String createAndRecordRoom(AuthorDocumentModel documentModel, URL systemId) {
    GroupChangesSaveStrategy saveStrategy = new GroupChangesSaveStrategy();
    String newRoomId = RoomsManager.INSTANCE.createRoomFromDocument(documentModel, saveStrategy);

    // Observer must be initialized right away,
    // otherwise if initialized by the first save it won't see previous changes.
    Room room = RoomsManager.INSTANCE.getRoom(newRoomId)
        .orElseThrow(IllegalStateException::new);
    room.getObserver();

    roomIdsStore.setRoomId(systemId, newRoomId);
    EditingSessionContext editingContext = documentModel.getAuthorAccess().getEditorAccess().getEditingContext();
    editingContext.setAttribute(Room.ROOM_CREATOR_ATTRIBUTE, true);

    return newRoomId;
  }

  @Override
  public void editingSessionFailedToStart(
      String sessionId, String licenseeId, URL systemId, Map<String, Object> options) {
    logger.warn("Failed to open document {}", systemId.getPath());
    Lock lock = getLock(systemId);
    lock.unlock();
  }

  @Override
  public void editingSessionClosed(String sessionId, AuthorDocumentModel documentModel) {
    Lock lock = getLock(getDocUrl(documentModel));
    lock.lock();
    try {
      URL systemId = getDocUrl(documentModel);
      String roomId = roomIdsStore.getRoomId(systemId)
          .orElseThrow(IllegalStateException::new);

      // Close the room when it becomes empty.
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
    mapSystemIdToLock.computeIfAbsent(docUrl, d -> new ReentrantLock());
    return mapSystemIdToLock.get(docUrl);
  }

  /**
   * @param roomId The room Id.
   * @return the peers within the given room.
   */
  private AtomicInteger getPeersCounter(String roomId) {
    mapRoomIdToPeersCounter.computeIfAbsent(roomId, d -> new AtomicInteger());
    return mapRoomIdToPeersCounter.get(roomId);
  }
}
