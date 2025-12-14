// src/components/PrintButton/PrintButton.jsx
import React from 'react';
import { Button } from '@mui/material';
import PrintIcon from '@mui/icons-material/Print';

const PrintButton = ({ onClick, ...props }) => {
  const handlePrint = () => {
    if (onClick) {
      onClick();
    } else {
      window.print();
    }
  };

  return (
    <Button
      variant="outlined"
      startIcon={<PrintIcon />}
      onClick={handlePrint}
      {...props}
    >
      Yazdır
    </Button>
  );
};

export default PrintButton;

