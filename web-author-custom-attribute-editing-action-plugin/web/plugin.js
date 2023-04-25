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
    dialogContainerElement.appendChild(this.rederParagrapthWithDescription_());
    let selectElement = this.rederCombobox_();
    dialogContainerElement.appendChild(selectElement);

    dialog.onSelect(function(key) {
      if (key === "ok") {
        let selectedValue = selectElement.options[selectElement.selectedIndex].text;
        done(selectedValue);
      } else {
        // Cancel button was pressed.
        done(currentValue);
      }
    });
    dialog.show();
  };


  rederParagrapthWithDescription_() {
    let dialogParagrapth = document.createElement("p");

    let dialogParagrapthText = document.createTextNode("Choose attribute value for attribute:");
    dialogParagrapth.appendChild(dialogParagrapthText);
    return dialogParagrapth;
  }

  rederCombobox_() {
    let selectEl = document.createElement("select");
    for (let i = 0; i < 10; i++) {
      let selectOption = document.createElement("option");
      selectEl.appendChild(selectOption);
      selectOption.setAttribute("value", "value" + i);
      selectOption.appendChild(document.createTextNode("value" + i));
    }
    return selectEl;
  }
}