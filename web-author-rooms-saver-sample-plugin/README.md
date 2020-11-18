Web Author Rooms Saver
=================================

Sample plugin that save to a CMS a concurrently edited document within a room*:
It uses the _ro.sync.ecss.extensions.api.webapp.ce.RoomObserver_ API
and whenever a user make an edit, integrates the edit and save it into CMS.

The documents are saved directly to the CMS, bypassing the custom URLStreamHandler,
with a user and a password retrieved from "cms-user" and "cms-password" options.

Note: the API allows to commit in batches, not necessarily after each edit.

*A room is an abstraction for a set of document models created for the same document.
Such models belong to different users and are edited concurrently and synchronized in real-time.
