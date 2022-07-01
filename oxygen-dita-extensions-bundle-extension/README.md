# Oxygen DITA-Extensions-Bundle Extension
======================================

This projects builds an Oxygen plugin [1] that contributes a DITA (framework) extension [2] that sets a custom [ExtensionsBundle](https://www.oxygenxml.com/InstData/Editor/SDK/javadoc/ro/sync/ecss/extensions/api/ExtensionsBundle.html) implementation [3] that extends the built-in implementation.
- [1] see [plugin.xml](plugin.xml)
- [2] see [dita-extension.exf](dita-extension.exf)
- [3] see [CustomDitaExtensionsBundle.java](src/main/java/com/oxygenxml/demo/CustomDitaExtensionsBundle.java)

You can create a custom DITA extensions bundle where you can override built-in methods. 

Note that it works both for Oxygen XML Web Author and Oxygen XML Editor (and Oxygen XML Author).
