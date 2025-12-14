// src/utils/print.js

export const printPage = () => {
  window.print();
};

export const printElement = (elementId) => {
  const element = document.getElementById(elementId);
  if (!element) {
    console.error(`Element with id ${elementId} not found`);
    return;
  }

  const printWindow = window.open('', '_blank');
  printWindow.document.write(`
    <html>
      <head>
        <title>Yazdır</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 20px; }
          @media print {
            body { margin: 0; }
            .no-print { display: none; }
          }
        </style>
      </head>
      <body>
        ${element.innerHTML}
      </body>
    </html>
  `);
  printWindow.document.close();
  printWindow.print();
};

export const printPDF = async (url) => {
  const link = document.createElement('a');
  link.href = url;
  link.download = 'document.pdf';
  link.click();
};
