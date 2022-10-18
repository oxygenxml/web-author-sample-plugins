import {createRoot} from "react-dom/client";
import {ThemeProvider} from "@mui/material/styles";
import theme from "./theme.js";
import CssBaseline from "@mui/material/CssBaseline";
import App from "./App.js";
import * as React from "react";

function StepperEnhancer(element, editingSupport) {
  sync.formctrls.Enhancer.call(this, element, editingSupport);
}
goog.inherits(StepperEnhancer, sync.formctrls.Enhancer);

StepperEnhancer.prototype.enterDocument = function() {
  this.root = createRoot(this.formControl);
  this.root.render(
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <App />
      </ThemeProvider>,
  );
}
StepperEnhancer.prototype.exitDocument = function() {
  this.root.unmount();
}

export default StepperEnhancer;