import StepperComponent from "./StepperComponent.js";
import StepperEnhancer from "./StepperEnhancer.js";

customElements.define('x-stepper', StepperComponent);
// workspace.listen(sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
//   e.editor.registerEnhancer(
//       'com.oxygenxml.web_author.react.StepperRenderer', StepperEnhancer);
// });
