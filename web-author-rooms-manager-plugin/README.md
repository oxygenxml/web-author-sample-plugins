Web Author Rooms Manager
=================================

Sample plugin that manages rooms*:
- assign each opened document to an already existing room or create a new one
- rooms are created with a custom save strategy to save changes made by peers on behalf of the committer (the one requests to save). See GroupChangesSaveStrategy
- if two users open the same document, they will concurrently edit the document
- close rooms that become empty 

*A room is an abstraction for a set of document models created for the same document.
Such models belong to different users and are edited concurrently and synchronized in real-time.

---

Notice that it's recommended to also enable the [always.send.keepalive](https://www.oxygenxml.com/doc/ug-waCustom/topics/customizing-options.html) option to mitigate dangling rooms for the closed tabs which fail to notice server about the disposed of the session. When closing the tab there is fired a dispose request that in some cases might not reach the server, leading to the WebappEditingSessionLifecycleListener.editingSessionClosed() server-side method being fired by default much later on, when the server-side session expires.
