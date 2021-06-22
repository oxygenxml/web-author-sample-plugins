Remove a prefix from all aboslute references
============================================


This plugin resolves absolute references after removing a prefix of them to make them relative.

For example, assume that all content is stored in a folder called `Content` in the root of a web server, in a DITA topic there is an image reference as below:

```xml
<image href="/Content/image.png"/>
```

If the folder `/Content/` is moved to, say, `/project1/Content`, all the references will be broken. This plugin installs an `URIResolver` that removes the `/Content/` prefix of the references, making them relative references.

The prefix can be changed in the `wsAccess.js` file.
