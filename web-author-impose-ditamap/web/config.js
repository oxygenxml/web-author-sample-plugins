window.ditaMapConfig = {
  // =================================================================================================================
  // If your project has the following layout:
  //  - all topics are located under a `topics/` folder
  //  - the DITA Map is in the root folder.
  //
  // Set the value of the `ditaMapFileName` property below to the name of your DITA Map file. Make sure that it is
  // enclosed in single quotes.
  // =================================================================================================================
  singleTopicLayout: {
    ditaMapFileName: 'UserManual.ditamap'
  },

  // =================================================================================================================
  // If all your users work with the same DITA Map, you should:
  // 1. Open that DITA Map in Web Author.
  // 2. Copy from from the browser's address bar and the part of the address after '?'.
  // 3. Paste it in the value of the the queryString property. Make sure that it is enclosed in single quotes.
  // =================================================================================================================
  singleDITAMap: {
    queryString: ''
  },
};