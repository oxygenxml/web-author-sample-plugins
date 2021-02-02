Web Author Custom UI Style Plugin
===============================

Plugin that loads a custom CSS to affect the user interface.

This plugin uses the WebappStaticResourcesFolder plugin extension to expose the `web/static` folder at
`http://example.com/oxygen-xml-web-author/plugin-resources/custom-ui-style-plugin/`.

The `plugin.js` file adds a `link` html element to the head of the Document 
to load the `web/static/style.css` file in the Web Author HTML page.

The `style.css` file changes the color of the header-bar at the top of the page. Add your own custom CSS here as needed. 

