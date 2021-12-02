package com.oxygenxml;

import java.util.List;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.editor.validation.ValidationProblems;
import ro.sync.exml.workspace.api.editor.validation.ValidationProblemsFilter;

public class CustomValidationProblemsFilter extends ValidationProblemsFilter {
  @Override
  public void filterValidationProblems(ValidationProblems validationProblems) {
    List<DocumentPositionedInfo> problemsList = validationProblems.getProblemsList();
    
    problemsList.removeIf(problem -> {
      // we remove all the validation warnings for demonstration purposes 
      // but more complex use-cases can be covered
      return problem.getSeverity() == DocumentPositionedInfo.SEVERITY_WARN;
    });
    
  }

}
