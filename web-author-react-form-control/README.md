Web Author Form Control implemented with React
==============================================

A plugin that implements a Form Control using React.


To work on the React code and have results available in the browser after a refresh, run:
```
npm run watch
```

To add the form control in a topic, you can add the following rule
to the framework CSS:

```css
[outputclass="stepper"] {
  content: oxy_editor(
    webappRendererClassName, "com.oxygenxml.web_author.react.StepperRenderer"
    edit, "custom");
}
```
