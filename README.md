# web-author-sample-plugins
Sample plugins for Oxygen XML Web Author

## Installation

Each folder contains one plugin. To install such a plugin you should:
 1. Download the entire repository 
 1. If applicable, follow the instructions included in the `README.md` file of the desired plugin.
 1. Create an archive with just the folder that contains the desired plugin
 1. Go to the Web Author **Administration Page**
 1. In the **Plugins** section, click **Upload Plugin** and choose the archive made at step 2.
 1. Restart the Web Author server.

## Plugins

### [always-enable-accept-change](always-enable-accept-change)
This plugin enables the "Accept Chage" action, even when change tracking is forced.

### [content-fusion-full-map-approvals](content-fusion-full-map-approvals)
This plugin can be installed in the Web Author editing component deployed in Content Fusion.

The plugin activates when a task contains the "[APPROVAL]" token in the title and forces users to review topics in the context of the DITA Map with expanded topics.

### [oxygen-dita-extensions-bundle-extension](oxygen-dita-extensions-bundle-extension)
Sample plugin that contributes a framework that extends the built-in DITA framework. The use case is to set a custom ExtensionsBundle implementation that extends the built-in implementation.

**_NOTE:_** if you don't need to specifically provide the framework via a plugin, it's simpler to add the framework directly.
Here you can find the similar, standalone framework: [https://github.com/oxygenxml-incubator/oxygen-sample-frameworks/tree/main/dita-extension-custom-proposed-attribute-values](https://github.com/oxygenxml-incubator/oxygen-sample-frameworks/tree/main/dita-extension-custom-proposed-attribute-values)

### [remove-prefix-uri-resolver](remove-prefix-uri-resolver)
This plugin resolves absolute references after removing a prefix of them to make them relative.

For example, assume that all content is stored in a folder called `Content` in the root of a web server, in a DITA topic there is an image reference as below:
```xml
<image href="/Content/image.png"/>
```

If the folder `/Content/` is moved to, say, `/project1/Content`, all the references will be broken. This plugin installs an `URIResolver` that removes the `/Content/` prefix of the references, making them relative references.

### [user-name-insert-operation-plugin](user-name-insert-operation-plugin)
This plugin contributes an equivalent of InsertFragmentOperation, but which support an ${user.name} editor variable that expands to the name of the current user.

### [web-author-CALS-table-plugin](web-author-CALS-table-plugin)
Sample plugin that adds the following CALS Table support for any XML document:
 - table rendering
 - specific actions are added before each table to insert or delete rows and columns and to join or split cells
 - multiple rows and columns can be selected (by mouse drag or click on column header markers) to easily copy or move table data by using copy/paste

### [web-author-add-non-persistent-highlights](web-author-add-non-persistent-highlights)
Sample plugin that add non persistent highlights on each paragraph when the document is opened and edited.
 
### [web-author-autospellcheck-on-off](web-author-autospellcheck-on-off)
This sample plugin adds a button to the toolbar to allow users to easily enable or disable the autospellcheck.

### [web-author-block-deleting-comments](web-author-block-deleting-comments)
Plugin that blocks a user from deleting review comments added by another user.

### [web-author-block-deleting-suggestions](web-author-block-deleting-suggestions)
Plugin that blocks a user from deleting tracked changes made by another user.

### [web-author-block-save-if-invalid](web-author-block-save-if-invalid)
Plugin that blocks saving invalid documents.

### [web-author-comment-eol-fixup](web-author-comment-eol-fixup)
Plugin that fixes the line separator in review comments to Linux-style. This plugin registers an AuthorDocumentFilter to modify review comments before they are inserted. It does not fix the exisiting comments.

### [web-author-document-preview-dialog-plugin](web-author-document-preview-dialog-plugin)
This sample plugin uses the editor.previewAuthorDocument to let the user preview another XML document inside a dialog. The user has to browse for a file, then the file will be displayed inside a dialog.

### [web-author-elements-view](web-author-elements-view)
A plugin that contributes an 'Elements' view that displays elements that can be inserted at the caret position

### [web-author-file-open-action](web-author-file-open-action)
Oxygen XML Web Author plugin that contributes an "Open..." action to the "More..." (triple dots) menu that lets the user browse for a file and open it in the current editor.

### [web-author-filter-extension](web-author-filter-extension)
This plugin implements a custom servlet filter plugin extension.

As an use-case it blocks requests to the dashboard page by redirecting to an external URL defined by com.oxygenxml.ServletFilterExtension.REDIRECT_URL (https://www.oxygenxml.com/ as an example).

### [web-author-react-form-control](web-author-react-form-control)
A plugin that implements a Form Control using React.

### [web-author-set-attribute-on-new-paragraphs](web-author-set-attribute-on-new-paragraphs)
Plugin that adds "normal" outputclass attribute to paragraph elements that have no outputclass set, when inserted in the document.

### [web-author-static-page](web-author-static-page)
This plugin hosts a static Web Page on the Web Author server. The page is accessible at:

http://example.org:8443/oxygen-xml-web-author/plugin-resources/web-root/index.html

Where the Web Author's Dashboard is located at:

http://example.org:8443/oxygen-xml-web-author/app/oxygen.html

### [web-author-table-cell-computed-values](web-author-table-cell-computed-values) 
Plugin that allows you to add automatically-computed values in a table cell.

### [web-author-validation-problems-filter](web-author-validation-problems-filter)
Sample plugin that removes all the warnings from the validation result list.

### [web-author-view-file-as-text](web-author-view-file-as-text)
Plugin contributes an action to choose a file and view it as text.

It performs the following steps:
- Show a file browser so that the user can choose the file
- Invoke a server-side operation to retrieve the content of the file
- The server-side operation connects to the CMS to read the content of the file and sends it back to the browser. The authentication context of the main document (the one opened in the editor) is used when loading this file.
- The file content is displayed in an alert in the browser.

### [web-author-rooms-manager-plugin](web-author-rooms-manager-plugin)
Sample plugin that manages rooms*:
- assign each opened document to an already existing room or create a new one
- rooms are created with a custom save strategy to save changes made by peers on behalf of the committer (the one requests to save). See GroupChangesSaveStrategy
- if two users open the same document, they will concurrently edit the document
- close rooms that become empty 

*A room is an abstraction for a set of document models created for the same document.
Such models belong to different users and are edited concurrently and synchronized in real-time.

### [web-author-side-by-side-editors](web-author-side-by-side-editors)
Is a plugin that presents two side by side Web Author editors in preview mode.

Before each element that has the _id_ attribute a button it is presented. When the button is clicked the other editor scrolls to the element having the same value for the _id_ attribute.

### [web-author-change-ui-with-css](web-author-change-ui-with-css)
Plugin that loads a custom CSS to affect the user interface.

### [web-author-loading-options-setter](web-author-loading-options-setter)
Sample plugin that imposes some loading options depending on the file extension:
 - Files ending in `.dita` open with the "no tags" mode.
 - Files ending in `.ditamap` or `.bookmap` are opened with the **Show topic titles** alternate style activated.

### [web-author-impose-options](web-author-impose-options)
Oxygen XML Web Author stores its options in an options.xml file, located in the options folder of the Oxygen Data Directory.

This is a sample plugin for imposing specific options. The `tagless.editor.tags.display.mode` option value is set to 1,
meaning that all the documents are opened with `No tags` mode activated.

### [web-author-impose-ditamap](web-author-impose-ditamap)
Sample plugin that imposes a DITA Map for all files opened in Web Author. It assumes a common
project folder layout in which:
 - the DITA Map is called `UserManual.ditamap`
 - all topics are located under a `topics/` folder
 
### [web-author-block-safe-if-invalid](web-author-block-safe-if-invalid)
Plugin that blocks saving invalid documents.

### [web-author-hide-app-bar](web-author-hide-app-bar)
Plugins that hides the Web Author app bar, usefull when integrating the Web Author in an iframe.

This plugin only contributes a single client-side javascript file.

### [web-author-filter-content](web-author-filter-content)
Plugin that hides the content based on a CSS selector. For demonstration purposes it hides the DITA elements that have the "processing-role" attribute set to "resource-only".

### [web-author-paste-actions](web-author-paste-actions)
Plugin that replaces the "Paste Special" action with two individual actions: `Paste as Text` (Ctrl+Alt+T) and `Paste as XML` (Ctrl+Alt+X).

### [web-author-comment-only](web-author-comment-only)
Plugin that switches Web Author to a comments-only mode.

### [web-author-custom-author-style](web-author-custom-author-style)
Plugin that enforces a custom stylesheet for all XML documents loaded in Web Author.

### [web-author-filter-styles](web-author-filter-styles)
Plugin that adds a Styles Filter that can be used to replace the CSS styles associated to an element or a pseudo element.

### [web-author-insert-all-elements](web-author-insert-all-elements)
Sample plugin that allows inserting elements that are not valid at caret position, by using the `Insert Element...` action from the contextual menu.

### [web-author-xslt-report](web-author-xslt-report)
Sample plugin to run an XSLT script over the current document and return the result to the client as string.

### [web-author-view-final-track-changes](web-author-view-final-track-changes)
Oxygen XML Web Author plugin that defaults the track changes view mode to `View Final`.

### [web-author-custom-attribute-editing-action-plugin](web-author-custom-attribute-editing-action-plugin)
Example plugin for Oxygen XML Web Author that defines a custom action for attributes editing.
It implements the [sync.actions.AttributeEditingActionsProvider](https://www.oxygenxml.com/maven/com/oxygenxml/oxygen-webapp/25.1.0.0/jsdoc/sync.actions.AttributeEditingActionsProvider.html) JS API.
The sync.actions.AttributeEditingActionsProvider JS API extension should be used when you need to implement a custom UI (or dialog) for choosing attribute values. Another use-case would be if the attribute values are dynamic, either computed client-side or fetched from other servers.

## Copyright and License
Copyright (C) 2020 Syncro Soft SRL. All rights reserved.

This project is licensed under Apache License 2.0.
