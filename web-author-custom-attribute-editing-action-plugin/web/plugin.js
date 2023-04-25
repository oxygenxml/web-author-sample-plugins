workspace.listen(sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
  let editor = e.editor;
  editor.registerAttributeActionsProvider(new CustomAttributeEditingActionsProvider());
});


class CustomAttributeEditingActionsProvider extends sync.actions.AttributeEditingActionsProvider {
  getAction(element, attributeName) {
    // Use the CustomAttributeEditingAction for all attributes for all elements for demo purposes.
    return new CustomAttributeEditingAction();
  };
}


class CustomAttributeEditingAction extends sync.actions.AttributeEditingAction {
  getButtonClass() {
    return 'Edit24';
  };

  actionPerformed(currentValue, done) {
    let dialog = workspace.createDialog();
    dialog.setPreferredSize(400, 300);
    dialog.setTitle("Choose Attribute Value");
    dialog.setButtonConfiguration(sync.api.Dialog.ButtonConfiguration.OK_CANCEL);

    let dialogContainerElement = dialog.getElement();
    let dialogParagrapth = document.createElement("p");
    dialogContainerElement.appendChild(dialogParagrapth);
    let dialogParagrapthText = document.createTextNode("Choose attribute value for attribute:");
    dialogParagrapth.appendChild(dialogParagrapthText);

    let selectEl = document.createElement("select");
    dialogContainerElement.appendChild(selectEl);

    let selectOption1 = document.createElement("option");
    selectEl.appendChild(selectOption1);
    selectOption1.setAttribute("value", "value1");
    selectOption1.appendChild(document.createTextNode("value1"));

    let selectOption2 = document.createElement("option");
    selectEl.appendChild(selectOption2);
    selectOption2.setAttribute("value", "value2");
    selectOption2.appendChild(document.createTextNode("value2"));

    let selectOption3 = document.createElement("option");
    selectEl.appendChild(selectOption3);
    selectOption3.setAttribute("value", "value3");
    selectOption3.appendChild(document.createTextNode("value3"));

    dialog.onSelect(function(key) {
      if (key === "ok") {
        let selectedValue = selectEl.options[selectEl.selectedIndex].text;
        done(selectedValue);
      } else {
        // Cancel button was pressed.
        done(currentValue);
      }
    });
    dialog.show();
  };
}