import {createRoot} from "react-dom/client";
import {ThemeProvider} from "@mui/material/styles";
import theme from "./theme.js";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import HorizontalLinearStepper from "./HorizontalStepper.js";
import Box from "@mui/material/Box";

function StepperEnhancer(element, editingSupport) {
  sync.formctrls.Enhancer.call(this, element, editingSupport);
}
goog.inherits(StepperEnhancer, sync.formctrls.Enhancer);

StepperEnhancer.prototype.getSteps = function() {
  const parentNode = this.getParentNode();
  return [...parentNode.childNodes]
      .map(child => child.childNodes[0].textContent);
};

StepperEnhancer.prototype.showChild = function(index) {
  const htmlElement = this.getParentNode().getHtmlNode();
  htmlElement.setAttribute('data-show-child', '' + index);
}
StepperEnhancer.prototype.enterDocument = function() {
  this.formControl.style.width = '100%';
  this.formControl.style.display = 'block';
  this.showChild(1);

  const steps = this.getSteps();
  this.root = createRoot(this.formControl);
  this.root.render(
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box sx={{ my: 4 }}>
          <HorizontalLinearStepper
              steps={steps}
              onStepChanged={activeStep => this.showChild(activeStep)}
          />
        </Box>
      </ThemeProvider>,
  );
}
StepperEnhancer.prototype.exitDocument = function() {
  this.root.unmount();
}

export default StepperEnhancer;