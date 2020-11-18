package com.oxygenxml.demo.roomssaver;

import java.net.URL;

import ro.sync.ecss.extensions.api.webapp.ce.Room;
import ro.sync.ecss.extensions.api.webapp.ce.RoomObserver;
import ro.sync.ecss.extensions.api.webapp.ce.RoomObserver.EditListener;

/**
 * Listen for peer edits and save them right away by using a <code>SyncListener</code>.
 */
class EditListenerImpl implements EditListener {
  /**
   * Listener invoked when synchronizing the observer with peer edits.
   */
  private SyncListenerImpl syncListener;

  /**
   * Observer that keeps document state.
   */
  private RoomObserver observer;

  /**
   * Constructor.
   * @param docUrl The document URL that is concurrently edited inside the given room.
   * @param room The room.
   */
  EditListenerImpl(URL docUrl, Room room) {
    this.observer = room.getObserver();
    this.syncListener = new SyncListenerImpl(docUrl, room);
  }

  @Override
  public void editHappened() {
    observer.sync(syncListener);
  }
}
