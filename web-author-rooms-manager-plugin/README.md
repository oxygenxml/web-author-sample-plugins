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
In order to compile it you need to::
* take the oxygen.jar file from the Oxygen XML Web Author 23.1.1 WAR [1]. The file is located inside WEB-INF/lib/oxygen.jar.
* place it inside the ./oxygen-xml-web-author-23.1.1/WEB-INF/lib/oxygen.jar directory (Note the dependecy with scrope system defined in the pom.xml file [2]).

[1] https://www.oxygenxml.com/xml_web_author/download_oxygenxml_web_author.html
[2] [pom.xml|https://raw.githubusercontent.com/oxygenxml/web-author-sample-plugins/master/web-author-rooms-manager-plugin/pom.xml].
