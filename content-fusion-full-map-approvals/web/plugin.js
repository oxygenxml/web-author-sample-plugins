(function() {
  /**
   * Activate the plugin only when the reivew task is marked as an approval task.
   */
  const shouldOpenFullMap = window.parent.document.title.includes('[APPROVAL]')
  if (!shouldOpenFullMap) {
    return;
  }

  workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
    var mapUrl = e.options.ditamap;
    if (mapUrl && !e.options.diffUrl) {
      if (removeAnchor(e.options.url) !== mapUrl) {
        // User was trying to open a topic - open the map instead.
        e.options.topicToFocus = e.options.url;
        e.options.url = mapUrl;
      }
      e.options.editReferencesInPlace = 'true';
      scrollToTopicOnLinkOpened(e.editor, mapUrl);
    }
  });

  const scrollToTopicOnLinkOpened = function(editor, mapUrl) {
    editor.listen(sync.api.Editor.EventTypes.LINK_OPENED, function (linkOpenedEvent) {
      if (linkOpenedEvent.external) {
        // External links should work normally.
        return;
      }
      // This is used by the "Edit reference" action which is not needed anyway.
      linkOpenedEvent.$actualUrl = null;

      var openedUrl = removeAnchor(linkOpenedEvent.url);
      var topic = getTopicElementByUrl(editor, openedUrl);
      if (topic) {
        var topicId = topic.getAttribute('id');
        linkOpenedEvent.url = mapUrl + '#' + topicId;
      } else {
        linkOpenedEvent.url = mapUrl;
      }
    }, true);
  };

  const removeAnchor = function(url) {
    var anchorStart = url.lastIndexOf('#');
    if (anchorStart !== -1) {
      url = url.substring(0, url.indexOf('#'));
    }
    return url;
  };

  const getTopicElementByUrl = function(editor, topicUrl) {
    var editingSupport = editor.getEditingSupport();
    var doc = editingSupport.getDocument();
    var topics = doc.getElementsByTagName('*');
    return topics.find(topic => isTopicWithUrl(topic, topicUrl));
  };

  const isTopicWithUrl = function(topic, topicUrl) {
    var classAttr = topic.getAttribute('class');
    if (classAttr && classAttr.split(' ').includes('topic/topic')) {
      return topicUrl === topic.getBaseURI();
    }
    return false;
  };

  workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
    var topicUrl = e.editor.options.topicToFocus;
    if (topicUrl) {
      scrollToTopicWithUrl(e.editor, topicUrl);
    }
  });

  const scrollToTopicWithUrl = function(editor, topicUrl) {
    var topicToScrollTo = getTopicElementByUrl(editor, topicUrl);
    var editingSupport = editor.getEditingSupport();
    var selectionManager = editingSupport.getSelectionManager();

    var selection = selectionManager.createEmptySelectionInNode(topicToScrollTo, 'before');
    selectionManager.setSelection(selection);

    // Scroll to the selection (small hack to fix a case in which images load later and change the layout)
    var selectionAfter = selectionManager.createEmptySelectionAfterNode(topicToScrollTo);
    var selectionAroundTopic = selectionAfter.extendedTo(topicToScrollTo, 0);
    selectionManager.scrollSelectionIntoView(selectionAroundTopic);
    setTimeout(function() {
      selectionManager.scrollSelectionIntoView(selectionAroundTopic);
    }, 1000);
  };
})();
