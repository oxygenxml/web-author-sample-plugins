Web Author XSLT Report Plugin
=============================

Sample plugin to run an XSLT script over the current document and return the result to the client as string.

To execute the report that collects all the IDs, run

```js
editor.getActionsManager().invokeOperation(
    'com.oxygenxml.XSLTReportOperation', 
    {script: 'scripts/collect-ids.xsl'},
    (err, result) => console.log(result.split(',')), // print the result
    null,
    true // This is a background operation - it does not update the document
);
```

Additional scripts can be added to the scripts folder.