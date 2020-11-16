package com.oxygenxml.demo.roomsmanager;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ro.sync.basic.util.URLUtil;

/**
 * Store for room IDs.
 * Each opened document corresponds to a unique room ID.
 */
class RoomIdsStore {
  /**
   * Map from document URL to its room ID.
   */
  private Map<URL, String> mapSystemIdToRoomId = new HashMap<>();

  /**
   * @param docUrl The document URL.
   * @return Optional room ID.
   */
  public Optional<String> getRoomId(URL docUrl) {
    docUrl = URLUtil.clearUserInfo(docUrl);
    return Optional.ofNullable(mapSystemIdToRoomId.get(docUrl));
  }
  /**
   * @param docUrl The document URL.
   * @param roomId The corresponding room ID.
   */
  public void setRoomId(URL docUrl, String roomId) {
    docUrl = URLUtil.clearUserInfo(docUrl);
    mapSystemIdToRoomId.put(docUrl, roomId);
  }
}
