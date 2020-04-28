Web Author Comments-Only Plugin
===============================

Plugin that switches Web Author to a comments-only mode.

The comments-only mode can be disabled using the query parameter: `comment-only=false`.

This plugin works by registering an `AuthorDocumentFilter` on the server to block any editing attempt. The browser UI is configured to also hide most of the editing controls:
- The document is opened in "review" mode.
- Removes some side-views and toolbar & context menu actions
