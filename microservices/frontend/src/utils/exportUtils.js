// src/utils/exportUtils.js

export const exportToExcel = (data, filename = 'export.xlsx') => {
  // Excel export implementation
  // Using a library like xlsx or similar
  console.log('Exporting to Excel:', data);
  // Implementation would go here
};

export const exportToPDF = async (data, filename = 'export.pdf') => {
  // PDF export implementation
  // Using a library like jsPDF or similar
  console.log('Exporting to PDF:', data);
  // Implementation would go here
};

export const exportToCSV = (data, filename = 'export.csv') => {
  const headers = Object.keys(data[0] || {});
  const csvContent = [
    headers.join(','),
    ...data.map(row => headers.map(header => row[header] || '').join(','))
  ].join('\n');

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = filename;
  link.click();
};

export const downloadFile = (url, filename) => {
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
};

