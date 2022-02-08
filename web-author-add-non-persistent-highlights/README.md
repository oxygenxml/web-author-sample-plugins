Web Author Add Non Persistent Highlights
=================================

Sample plugin that add non persistent highlights on each paragraph:
 - when the document is opened (invoking an AuthorOperation from the javascript code, on the sync.api.Workspace.EventType.EDITOR_LOADED event)
 - when the document is edited (using an AuthorDocumentFilter) 
 
The highlights style are imposed by using a custom CSS file, loaded using a WebappStaticResourcesFolder extension.
