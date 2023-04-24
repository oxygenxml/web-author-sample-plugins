# Oxygen DITA-Extensions-Bundle Extension
======================================

This projects builds an Oxygen plugin [1] that contributes a DITA (framework) extension [2] that sets a custom [ExtensionsBundle](https://www.oxygenxml.com/InstData/Editor/SDK/javadoc/ro/sync/ecss/extensions/api/ExtensionsBundle.html) implementation [3] that extends the built-in implementation.
- [1] see [plugin.xml](plugin.xml)
- [2] see [dita-extension.exf](dita-extension.exf). Read more about Framework Extension Script File [in documentation](https://www.oxygenxml.com/doc/ug-editor/topics/framework-customization-script-usecases.html)
- [3] see [CustomDitaExtensionsBundle.java](src/main/java/com/oxygenxml/demo/CustomDitaExtensionsBundle.java)

This plugin works for both Oxygen XML Web Author and Oxygen XML Author/Editor.

**_NOTE:_** if you don't need to specifically provide the framework via a plugin, it's simpler to add the framework directly.
Here you can find the similar, standalone framework: [https://github.com/oxygenxml-incubator/oxygen-sample-frameworks/tree/main/dita-extension-custom-proposed-attribute-values](https://github.com/oxygenxml-incubator/oxygen-sample-frameworks/tree/main/dita-extension-custom-proposed-attribute-values)
