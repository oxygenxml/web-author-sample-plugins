
# Web Author Table Pagination Plugin (for Web Author v27.1+)

==============================================

A plugin that adds a table pagination functionality to tables with more than 10 rows. 

This can improve performance when editing large tables.

![Plugin in action](./img/table-pagination.gif)

How to build the plugin
-----------------------

Run the following commands:
```
npm install
npm run compile
mvn package
```

The plugin archive will be available in the `target/web-author-react-plugin-<version>-plugin.jar`.

How to develop the plugin
-------------------------

Run the following commands:
```
npm install
mvn compile
npm run watch
```

Then, follow the instructions here: https://www.oxygenxml.com/doc/ug-waCustom/topics/webapp-plugin-prototyping.html

Our customization guide contains some more details about implementing a custom form control: https://www.oxygenxml.com/doc/ug-waCustom/topics/customizing_frameworks.html#customizing_frameworks__li_bgs_dgk_54b .
