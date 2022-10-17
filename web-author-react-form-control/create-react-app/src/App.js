import * as React from 'react';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import HorizontalLinearStepper from "./HorizontalStepper.js";


export default function App() {
  return (
    <Container maxWidth="sm">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Horizontal Stepper
        </Typography>
        <HorizontalLinearStepper/>
      </Box>
    </Container>
  );
}
