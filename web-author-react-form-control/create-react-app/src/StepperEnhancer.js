import {createRoot} from "react-dom/client.js";
import {ThemeProvider} from "@mui/material/styles";
import theme from "./theme.js";
import CssBaseline from "@mui/material/CssBaseline";
import App from "./App.js";
import * as React from "react";

export default class StepperEnhancer extends sync.formctrls.Enhancer {

  enterDocument() {
    this.root = createRoot(this.formControl);
    this.root.render(
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <App />
        </ThemeProvider>,
    );
  }
  exitDocument() {
    this.root.unmount();
  }
}