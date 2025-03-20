// Register the table pagination renderer
workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  e.editor.registerEnhancer(
      'com.oxygenxml.web_author.pagination.TablePaginationRenderer', TablePagination);
});


// Disable table drag handler for performance
workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, (e) => {
  sync.table.TableDragHandleInstaller.prototype.attach = () => {};
});

// Add a stripe to suggest no tags mode
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

    // Create the button element
    let button = document.createElement('button');
    
    let img = document.createElement('img');
    img.src = "images/no-sprite/NoTags16.png";
    img.alt = "No Tags";
    img.style.verticalAlign = "middle";
    img.style.marginRight = "5px";
    
    // Append the image to the button
    button.appendChild(img);
    
    // Append the button text after the image
    let buttonText = document.createTextNode("Hide Tags");
    button.appendChild(buttonText);
    
    // Apply the specified inline styles to the button
    button.style.color = "#337AB7";
    button.style.padding = "2px 10px";
    button.style.cursor = "pointer";
    button.style.fontSize = "14px";
    button.style.display = "inline-flex";
    button.style.border = "1px solid";
    button.style.marginLeft = "10px";
    button.style.backgroundColor = "white";
    button.style.position = "absolute";
    button.style.right = "10px";
    
    // Define the button action
    button.onclick = () => {
      // Set the option to no-tags mode
      workspace.setOption(sync.OptionTags.SENTINELS_DISPLAY_MODE, 'no-tags');
      workspace.removeEditorStripe(stripe);
    };

    // Append the button to the stripe
    stripe.appendChild(button);
  }
});
