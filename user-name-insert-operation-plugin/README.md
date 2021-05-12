Insert operation plugin (aware of user name)
============================================

This plugin contributes an equivalent of InsertFragmentOperation, but which support an ${user.name} editor variable that expands to the name of the current user. The fully qualified name of the operation is: `com.oxygenxml.username.InsertFragmentOperation`.

The operation can be used in CSS as below:

```css
title:after{
    content: oxy_button(
       action, oxy_action(
         name, 'Add change item',
         arg-fragment, '<i>${user.name}</i>',
         operation, 'com.oxygenxml.username.InsertFragmentOperation'),
       showText, false);
}
```


