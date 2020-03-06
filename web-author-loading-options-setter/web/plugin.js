goog.events.listen(workspace, sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  if (e.options.url.endsWith('.dita')) {
    // DITA files will be opened in no-tags mode.
    e.options['tags-mode'] = 'no-tags';
  }

  if (e.options.url.endsWith('.ditamap')) {
    // Activate the stylesheet that activates topic titles for DITA Map files
    e.options['stylesheet-titles'] = 'Basic,+ Show topic titles';
  }
});
