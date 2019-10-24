(function () {
  workspace.getEditingSupportManager().registerEditingSupportProvider({
    getEditingSupport : function(contentType, editor) {
      var isSideBySideEditor = editor.options.rightUrl;
      if(isSideBySideEditor) {
        editor.registerEnhancer('ro.sync.servlet.plugin.GenericFormControlRenderer', sync.formctrls.ScrollFormControl);
      }

      return isSideBySideEditor ? new sync.sidebyside.SideBySideEditingSupport(editor) : null;
    }
  });
})();
