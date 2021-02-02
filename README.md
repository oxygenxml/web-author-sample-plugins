# web-author-sample-plugins
Sample plugins for Oxygen XML Web Author

## Installation

Each folder contains one plugin. To install such a plugin you should:
 1. Download the entire repository 
 1. Create an archive with just the folder that contains the desired plugin
 1. Go to the Web Author **Administration Page**
 1. In the **Plugins** section, click **Upload Plugin** and choose the archive made at step 2.

## Plugins

### web-author-side-by-side-editors
Is a plugin that presents two side by side Web Author editors in preview mode.
Before each element that has the _id_ attribute a button it is presented. When the button is clicked the other editor scrolls to the element having the same value for the _id_ attribute.

### web-author-change-ui-with-css
Plugin that loads a custom CSS to affect the user interface.

### web-author-loading-options-setter

Sample plugin that imposes some loading options depending on the file extension:
 - Files ending in `.dita` open with the "no tags" mode.
 - Files ending in `.ditamap` or `.bookmap` are opened with the **Show topic titles** alternate style activated.

### web-author-impose-options

Oxygen XML Web Author stores its options in an options.xml file, located in the options folder of the Oxygen Data Directory.
This is a sample plugin for imposing specific options. The "tagless.editor.tags.display.mode" option value is set to 1,
meaning that all the documents are opened with "no tags" mode activated.

### web-author-impose-ditamap

Sample plugin that imposes a DITA Map for all files opened in Web Author. It assumes a common
project folder layout in which:
 - the DITA Map is called `UserManual.ditamap`
 - all topics are located under a `topics/` folder
 
### web-author-block-safe-if-invalid

Plugin that blocks saving invalid documents.

### web-author-hide-filtered-content

Plugin that hides the content that is filtered out by a DITAVAL filter. By default Web Author shows this content greyed out.

### web-author-paste-actions

Plugin that replaces the "Paste Special" action with two individual actions: "Paste as Text" (Ctrl+Alt+T) and "Paste as XML" (Ctrl+Alt+X).

### web-author-comment-only

Plugin that switches Web Author to a comments-only mode.

### web-author-xslt-report

Sample plugin to run an XSLT script over the current document and return the result to the client as string.

## Copyright and License

Copyright (C) 2020 Syncro Soft SRL. All rights reserved.

This project is licensed under Apache License 2.0.
