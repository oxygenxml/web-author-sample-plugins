workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  e.editor.registerEnhancer(
      'com.oxygenxml.web_author.pagination.TablePaginationRenderer', TablePagination);
});


// Disable table drag handler for performance
workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, (e) => {
  sync.table.TableDragHandleInstaller.prototype.attach = () => {};
});

workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, (e) => {
  let isTagsForced = !!e.editor.options["tags-mode"];
  if (!isTagsForced && workspace.getOption(sync.OptionTags.SENTINELS_DISPLAY_MODE) !== 'no-tags') {
    
    // Create an editor stripe with height 27px
    let stripe = workspace.createEditorStripe(27);
    stripe.classList.add('flash-message-warn', 'vertical-align-children');

    // Create and append the text message
    let message = document.createElement('span');
    message.textContent = 'Switch to no-tags mode for better performance when editing large tables:';
    stripe.appendChild(message);

    // Create and append the button
    let button = document.createElement('button');
    button.textContent = 'Switch to no-tags mode';
    button.onclick = () => {
      // Set the option to no-tags mode
      workspace.setOption(sync.OptionTags.SENTINELS_DISPLAY_MODE, 'no-tags');
      workspace.removeEditorStripe(stripe);
    };
    stripe.appendChild(button);

  }
});
