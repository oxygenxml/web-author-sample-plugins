View File As Text - Plugin
==========================

Plugin contributes an action to choose a file and view it as text.

It performs the following steps:
- Show a file browser so that the user can choose the file
- Invoke a server-side operation to retrieve the content of the file
- The server-side operation connects to the CMS to read the content of the file and sends it back to the browser. The authentication context of the main document (the one opened in the editor) is used when loading this file.
- The file content is displayed in an alert in the browser.
