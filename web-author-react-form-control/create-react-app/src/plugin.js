import StepperEnhancer from "./StepperEnhancer.js";

workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
  e.editor.registerEnhancer(
      'com.oxygenxml.web_author.react.Stepper', StepperEnhancer);
});