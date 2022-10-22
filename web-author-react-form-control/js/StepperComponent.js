import {createRoot} from "react-dom/client";
import * as React from "react";
import theme from "./theme.js";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import HorizontalLinearStepper from "./HorizontalStepper.js";
import {ThemeProvider} from "@mui/material/styles";
import { CacheProvider as EmotionCacheProvider } from '@emotion/react';
import createCache from '@emotion/cache';
import StepperEnhancer from "./StepperEnhancer.js";

export default class StepperComponent extends HTMLElement {

  constructor() {
    super();
    this.showChild = this.showChild.bind(this);
    this.switchToEditMode = this.switchToEditMode.bind(this);
    this.editingSupport = workspace.currentEditor.getEditingSupport();
  }


  switchToEditMode() {
    console.log('here')
    let parentNode = this.getParentNode();
    let selectionManager = this.editingSupport.getSelectionManager();
    let selection = selectionManager.createEmptySelectionInNode(parentNode, 'before');
    this.editingSupport.getActionsManager().invokeOperation('SetPseudoClassOperation', {
      name: 'raw-edit'
    }, null, selection);
  };

  showChild(index, prevIndex) {
    const htmlElement = this.getParentNode().getHtmlNode();
    htmlElement.setAttribute('data-pseudoclass-show-child-' + index, 'true');
    htmlElement.removeAttribute('data-pseudoclass-show-child-' + prevIndex);
  }

  getParentNode() {
    return workspace.currentEditor.getEditingSupport().getDocument().createApiNodeOrNull(this);
  }

  getSteps() {
    const parentNode = this.getParentNode();
    return [...parentNode.childNodes]
        .map(child => child.childNodes[0].textContent);
  }

  connectedCallback() {
    this.attachShadow({ mode: 'open' });
    const mountPoint = this.shadowRoot;

    var id = String(this.getAttribute('data-id') );
    const emotionCache = createCache({
      key: [...id].map(char => String.fromCharCode(char.charCodeAt(0) - '0'.charCodeAt(0) + 'a'.charCodeAt(0))),
      container: mountPoint,
    });
    const root = createRoot(mountPoint);
    // var steps = this.getSteps();
    // console.log(steps)
    root.render(
        <EmotionCacheProvider value={emotionCache}>
          <ThemeProvider theme={theme}>
            <CssBaseline />
            <Box sx={{ my: 4 }}>
              <HorizontalLinearStepper
                  steps={["a", "b", "c"]}
                  onStepChanged={this.showChild}
                  onEdit={this.switchToEditMode}
              />
            </Box>
          </ThemeProvider>
        </EmotionCacheProvider>);
  }
}

