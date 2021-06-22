var URI = Packages.java.net.URI;
var URIResolver = Packages.javax.xml.transform.URIResolver;
var Source = Packages.javax.xml.transform.Source;
var System = Packages.java.lang.System;


var prefix = "/Content/";

function applicationStarted(pluginWorkspaceAccess) {
  var uriResolver = {
    resolve: function (childURL, baseURL) {
      if (childURL && childURL.indexOf(prefix) === 0) {
        childURL = childURL.substring(prefix.length);
        var resolvedURL = defaultResolve(baseURL, childURL);
        return createSource(resolvedURL);
      } else {
        return null;
      }
    }
  }
  pluginWorkspaceAccess.getXMLUtilAccess().addPriorityURIResolver(new JavaAdapter(URIResolver, uriResolver));
}

function defaultResolve(baseURL, childURL) {
  return new URI(baseURL).resolve(childURL).toString();
}

function createSource(systemId) {
  return new JavaAdapter(Source, { 
    getSystemId: function() { 
      return systemId;
    }, 
    setSystemId: function() {
    }
  });
}

function applicationClosing(pluginWorkspaceAccess) {
}
