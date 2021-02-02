(function () {
  /**
   * Dynamically load a CSS file in the document head.
   * @param {string} cssUrl The URL of the CSS to load.
   */
  function loadCSS(cssUrl) {
    var link = document.createElement('link');
    link.rel = "stylesheet";
    link.type = "text/css";
    link.href = cssUrl;
    document.head.appendChild(link);
  }

  // Load the `style.css` file in Web Author.
  loadCSS('../plugin-resources/custom-ui-style-plugin/style.css');
})();