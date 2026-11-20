# Web Author Editing Sessions Viewer

A sample Oxygen XML Web Author plugin that shows, in the **Administration Page**, the users that
currently have documents open.

## How it works

- `EditingSessionsTracker` (a `WorkspaceAccess` extension) registers a
  `WebappEditingSessionLifecycleListener` for documents and one for DITA Maps. On
  `editingSessionStarted` it records:
  - the author name: `documentModel.getReviewController().getReviewerAuthorName()`
  - the user id and licensee id: `documentModel.getUserId()` / `getLicenseeId()`
  - the document URL: `getAuthorDocumentController().getAuthorDocumentNode().getSystemID()`

  It also adds an `AuthorListener` on the document to record the time of the last modification,
  shown in the **Last edit** column. A session can stay open for hours after the user stopped
  working (for example when the tab is left open), so this column tells who is actually editing
  right now. Users who only read the document, without modifying it, have no last edit time.

  The session is marked idle on `editingSessionAboutToBeSerialized`, active again on
  `editingSessionDeserialized`, and removed on `editingSessionClosed`.
  The author name is read when the page is displayed, not when the session starts, because
  Web Author sets it right after notifying `editingSessionStarted`. It is the `userName` loading
  option (for example set from a client-side plugin on `sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED`
  with `e.options.userName = ...`), the authenticated user name, or "Anonymous".
- `EditingSessionsAdminPage` (a `ServletPluginConfigExtension`, contributed as `WebappServlet`
  with `role="config"`, which is what makes the **Configure...** button appear) renders a table
  with that information. Only authenticated administrators can access it.
- `web/static/admin-page.js` (served by a `WebappStaticResourcesFolder` extension) implements the
  **Refresh** button and hides the **Apply** / **Restore Defaults** buttons that the Administration
  Page adds to every plugin configuration dialog. It is an external file because the
  Administration Page Content-Security-Policy (`script-src 'self'`) blocks inline scripts.

## Install and use

1. Create an archive with just the folder of this plugin (the `lib` folder already contains the
   compiled code), or use the archive built with Maven (see below).
2. Administration Page -> Plugins -> **Upload Plugin** -> select the archive, then restart
   Web Author.
3. Administration Page -> Plugins -> **Editing sessions viewer** -> **Configure...**.
   The dialog lists the users, their open documents, when they were opened, when they were last
   edited and whether the session is active or idle. Use **Refresh** to update the list.

## Build

    mvn package

The plugin archive is `target/web-author-editing-sessions-plugin-1.0-SNAPSHOT-plugin.jar`.
It is built against Oxygen SDK 28.1 (change `oxygen.sdk.version` in `pom.xml` if needed;
`ServletPluginConfigExtension` requires version 26 or newer). Tested with Web Author 29.0.

If the Java code is modified, copy `target/build/web-author-editing-sessions-plugin-1.0-SNAPSHOT.jar`
to `lib/web-author-editing-sessions-plugin.jar`.

## Limitations

- The list is kept in memory: it is reset when the server restarts, and on a cluster each node
  only shows its own sessions.
- When a tab is closed normally, the browser notifies the server and the session is removed from
  the list. If that notification does not reach the server (browser crash, computer put to sleep,
  lost network connection), the session stays in the list: as active while it is kept in memory
  (12 hours after the last access, by default), then as idle until Web Author deletes it from disk
  (3 days, by default).
