Web Author Add Non Persistent Highlights
=================================

Sample plugin that add non persistent highlights on each paragraph:
 - when the document is opened (invoking an [AuthorOperation](src/main/java/com/oxygenxml/AddHighlightsOnParagraphsOperation.java) from the [JavaScript code](web/plugin.js#L5), on the sync.api.Workspace.EventType.EDITOR_LOADED event)
 - when the document is edited (using [an AuthorDocumentFilter](src/main/java/com/oxygenxml/CustomWorkspaceAccessPluginExtension.java#L34)) 
 
The highlights [are added with some attributes](src/main/java/com/oxygenxml/ParagraphsHighlighter.java#L39) that are [matched in CSS](web/static/custom-style.css) to assign a custom style to these highlights. 

The CSS file, loaded using a WebappStaticResourcesFolder extension.
