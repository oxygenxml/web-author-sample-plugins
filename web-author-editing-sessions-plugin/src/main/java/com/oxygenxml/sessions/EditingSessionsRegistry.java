package com.oxygenxml.sessions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;

/**
 * In-memory registry of the editing sessions currently known to this Web Author server.
 * <p>
 * It is shared (static) between the {@link EditingSessionsTracker} (which fills it from the
 * editing session lifecycle events) and the {@link EditingSessionsAdminPage} (which displays it
 * in the Administration Page). Both extensions are loaded by the same plugin class loader.
 */
public final class EditingSessionsRegistry {

  /**
   * The state of an editing session.
   */
  public enum State {
    /** The session is loaded in memory. */
    ACTIVE,
    /** The session was serialized to disk after a period of inactivity. */
    INACTIVE
  }

  /**
   * Information about one editing session.
   */
  public static final class SessionInfo {
    /** The editing session id. */
    final String sessionId;
    /** The kind of session: "Document" or "DITA Map". */
    final String kind;
    /** Licensee id (authenticated user or browser id). */
    final String licenseeId;
    /** User id, as known by Web Author. */
    volatile String userId;
    /** The author name (the name used for comments / track changes). */
    volatile String authorName;
    /** The URL of the edited document. */
    volatile String systemId;
    /** When the session was started. */
    final long startTime;
    /** Last time the session was (de)serialized or started. */
    volatile long lastActivity;
    /**
     * Last time the document content was modified in this session, 0 if it was not modified.
     * A session can stay open (and "active") for hours after the user stopped working, for
     * example when the tab is left open, so this tells who is actually editing right now.
     */
    volatile long lastEdit;
    /** The current state. */
    volatile State state;
    /**
     * The document model, while the session is active. Used to read the author name when the
     * page is displayed: Web Author sets the author name only AFTER notifying
     * editingSessionStarted, and the user can also change it later.
     */
    volatile AuthorDocumentModel documentModel;

    SessionInfo(String sessionId, String kind, String licenseeId, String userId, String authorName,
        String systemId) {
      this.sessionId = sessionId;
      this.kind = kind;
      this.licenseeId = licenseeId;
      this.userId = userId;
      this.authorName = authorName;
      this.systemId = systemId;
      this.startTime = System.currentTimeMillis();
      this.lastActivity = startTime;
      this.state = State.ACTIVE;
    }

    /**
     * Refreshes the author name from the document model, if the session is active.
     */
    void refreshAuthorName() {
      AuthorDocumentModel model = documentModel;
      if (model != null && state == State.ACTIVE) {
        try {
          authorName = model.getReviewController().getReviewerAuthorName();
        } catch (RuntimeException e) {
          // The model may have just been disposed, keep the last known name.
        }
      }
    }
  }

  /** Session id -> info. */
  private static final Map<String, SessionInfo> SESSIONS = new ConcurrentHashMap<>();

  /**
   * How long an inactive (serialized) session is kept in the list.
   * <p>
   * When Web Author deletes a serialized session from disk, it does not notify the
   * lifecycle listeners (editingSessionClosed is called only for sessions that are in memory).
   * Without this, the sessions of users who closed the browser tab without closing the document
   * would stay in the list until the server is restarted.
   * <p>
   * Web Author deletes serialized sessions 3 days after their last access by default (the
   * ".docs.disk.expire" datastore parameter). Since the time is measured here from the
   * serialization, which happens after the last access, the session is removed from this list
   * shortly after Web Author removes it, never before.
   */
  private static final long INACTIVE_SESSION_EXPIRE_MS = 3L * 24 * 60 * 60 * 1000;

  private EditingSessionsRegistry() {
  }

  static void sessionStarted(SessionInfo info) {
    SESSIONS.put(info.sessionId, info);
  }

  static void sessionClosed(String sessionId) {
    SESSIONS.remove(sessionId);
  }

  static void sessionSerialized(String sessionId) {
    SessionInfo info = SESSIONS.get(sessionId);
    if (info != null) {
      // Remember the last name, the model cannot be used after serialization.
      info.refreshAuthorName();
      info.documentModel = null;
      info.state = State.INACTIVE;
      info.lastActivity = System.currentTimeMillis();
    }
  }

  static SessionInfo get(String sessionId) {
    return SESSIONS.get(sessionId);
  }

  static void documentEdited(String sessionId) {
    SessionInfo info = SESSIONS.get(sessionId);
    if (info != null) {
      info.lastEdit = System.currentTimeMillis();
    }
  }

  /**
   * @return A snapshot of the known sessions, sorted by author name, then by start time.
   */
  static List<SessionInfo> getSessions() {
    long now = System.currentTimeMillis();
    SESSIONS.values().removeIf(s -> s.state == State.INACTIVE
        && now - s.lastActivity > INACTIVE_SESSION_EXPIRE_MS);

    List<SessionInfo> list = new ArrayList<>(SESSIONS.values());
    for (SessionInfo info : list) {
      info.refreshAuthorName();
    }
    Collections.sort(list, Comparator
        .comparing((SessionInfo s) -> s.authorName == null ? "" : s.authorName.toLowerCase())
        .thenComparingLong(s -> s.startTime));
    return list;
  }
}
