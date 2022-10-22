import * as React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';
import EditIcon from '@mui/icons-material/Edit';
import {IconButton} from "@mui/material";

export default function HorizontalLinearStepper({steps, onStepChanged, onEdit}) {
  const [activeStep, setActiveStep] = React.useState(0);
  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    onStepChanged(activeStep + 2, activeStep + 1);
  };
  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
    onStepChanged(activeStep, activeStep + 1);
  };

  return (
      <Box sx={{ width: '100%' }}>
        <Stepper activeStep={activeStep}>
          {steps.map((label, index) =>
                <Step key={label}>
                  <StepLabel>{label}</StepLabel>
                </Step>
          )}
        </Stepper>
        <slot/>
        <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
          <Button color="inherit" onClick={handleBack} disabled={activeStep === 0}>
            Back
          </Button>
          <Box sx={{ flex: '1 1 auto' }} />
          <Button onClick={handleNext} disabled={activeStep === steps.length - 1}>
            Next
          </Button>
          <Button startIcon={<EditIcon/>} onClick={onEdit}>
            Edit
          </Button>
        </Box>
      </Box>
  );
}
