# web-author-sample-plugins
Sample plugins for Oxygen XML Web Author

## Installation

Each folder contains one plugin. To install such a plugin you should:
 1. Download the entire repository 
 1. If applicable, follow the instructions included in the `README.md` file of the desired plugin.
 1. Create an archive with just the folder that contains the desired plugin
 1. Go to the Web Author **Administration Page**
 1. In the **Plugins** section, click **Upload Plugin** and choose the archive made at step 2.

## Plugins

### [always-enable-accept-change](always-enable-accept-change)
This plugin enables the "Accept Chage" action even when change tracking is forced.

### [content-fusion-full-map-approvals](content-fusion-full-map-approvals)
This plugin works can be installed in the Web Author editing component deployed in Content Fusion.
The plugin activates when a task contains the "[APPROVAL]" token in the title.
When active, the plugin forces users to review topics in the context of the DITA Map with expanded topics.

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

### [web-author-block-deleting-suggestions](web-author-block-deleting-suggestions)
Plugin that blocks a user from deleting tracked changes made by another user.

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
This is a sample plugin for imposing specific options. The "tagless.editor.tags.display.mode" option value is set to 1,
meaning that all the documents are opened with "no tags" mode activated.

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

### [web-author-hide-filtered-content](web-author-hide-filtered-content)
Plugin that hides the content that is filtered out by a DITAVAL filter. By default Web Author shows this content greyed out.

### [web-author-paste-actions](web-author-paste-actions)
Plugin that replaces the "Paste Special" action with two individual actions: "Paste as Text" (Ctrl+Alt+T) and "Paste as XML" (Ctrl+Alt+X).

### [web-author-comment-only](web-author-comment-only)
Plugin that switches Web Author to a comments-only mode.

### [web-author-custom-author-style](web-author-custom-author-style)
Plugin that enforces a custom stylesheet for all XML documents loaded in Web Author.

### [web-author-filter-styles](web-author-filter-styles)
Plugin that adds a Styles Filter that can be used to replace the CSS styles associated to an element or a pseudo element.

### [web-author-insert-all-elements](web-author-insert-all-elements)
Sample plugin that allows inserting elements that are not valid at caret position, by using the "Insert Element..." action from the contextual menu.

### [web-author-xslt-report](web-author-xslt-report)
Sample plugin to run an XSLT script over the current document and return the result to the client as string.

### [web-author-view-final-track-changes](web-author-view-final-track-changes)
Oxygen XML Web Author plugin that defaults the track changes view mode to final.


## Copyright and License
Copyright (C) 2020 Syncro Soft SRL. All rights reserved.

This project is licensed under Apache License 2.0.
