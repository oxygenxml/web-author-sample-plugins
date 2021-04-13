Comment Line Separator Fixup
============================

Plugin that fixes the line separator in review comments to Linux-style. This plugin registers an `AuthorDocumentFilter` to modify review comments before they are inserted. It does not fix the exisiting comments. 

It should be used with the `editor.line.separator.6.1.1` option set to `&amp;#xa;` to make line separators linux-styles in other places as well.