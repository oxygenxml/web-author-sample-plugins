package com.oxygenxml.demo.roomssaver;

import java.net.URL;
import java.util.Map;
import java.util.Optional;

import ro.sync.basic.util.URLUtil;
import ro.sync.ecss.extensions.api.access.EditingSessionContext;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.EditingSessionOpenVetoException;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.ce.Room;
import ro.sync.ecss.extensions.api.webapp.ce.RoomObserver;
import ro.sync.ecss.extensions.api.webapp.ce.RoomsManager;

/**
 * Installs the <code>EditListenerImpl</code>.
 * Usually the {@link EditListener} may be registered the same place where
 * {@link RoomsManager#createRoomFromDocument(AuthorDocumentModel)} is called.
 */
class EditListenerInstaller extends WebappEditingSessionLifecycleListener {
  @Override
  public void editingSessionStarted(String sessionId, AuthorDocumentModel documentModel) {
    EditingSessionContext context = documentModel
        .getAuthorAccess().getEditorAccess().getEditingContext();

    String roomId = (String) context.getAttribute(Room.ROOM_ID_ATTRIBUTE);
    Optional<Room> roomOpt = RoomsManager.INSTANCE.getRoom(roomId);
    Boolean isRoomCreator = (Boolean) context.getAttribute(Room.ROOM_CREATOR_ATTRIBUTE);

    if (roomOpt.isPresent() && isRoomCreator != null && isRoomCreator) {
      Room room = roomOpt.get();
      RoomObserver roomObserver = room.getObserver();

      URL docUrl = documentModel.getWSEditor().getEditorLocation();
      docUrl = URLUtil.clearUserInfo(docUrl);
      roomObserver.addEditListener(new EditListenerImpl(docUrl, room));
    }
  }

  @Override
  public void editingSessionAboutToBeStarted(
      String sessionId, String licenseeId, URL systemId, Map<String, Object> options)
    throws EditingSessionOpenVetoException {
    // Do nothing.
  }

  @Override
  public void editingSessionFailedToStart(
      String sessionId, String licenseeId, URL systemId, Map<String, Object> options) {
    // Do nothing.
  }

  @Override
  public void editingSessionClosed(String sessionId, AuthorDocumentModel documentModel) {
    // Do nothing.
  }
}
