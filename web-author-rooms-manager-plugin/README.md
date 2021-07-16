Web Author Rooms Manager
=================================

Sample plugin that manages rooms*:
- assign each opened document to an already existing room or create a new one
- rooms are created with a custom save strategy to save changes made by peers on behalf of the committer (the one requests to save). See GroupChangesSaveStrategy
- if two users open the same document, they will concurrently edit the document
- close rooms that become empty 

*A room is an abstraction for a set of document models created for the same document.
Such models belong to different users and are edited concurrently and synchronized in real-time.


NOTE: This plugin uses API from oxygen-sdk 23.1.1 that isn't released yet.
In order to compile it though, you must add oxygen.jar from Oxygen XML Web Author 23.1.1 to classpath.
To do so, specify a "system" dependency in pom.xml that points to the oxygen.jar from local disk,
file that must be manually unpacked from Web Author 23.1.1 WAR.