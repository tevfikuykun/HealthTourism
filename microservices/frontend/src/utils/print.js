/**
 * Print Utilities
 * 
 * Yazdırma işlemleri için yardımcı fonksiyonlar.
 */

/**
 * Belirli bir element'i yazdırır.
 * @param {string|HTMLElement} element - Yazdırılacak element veya ID
 */
export const printElement = (element) => {
  const printWindow = window.open('', '_blank');
  const elementToPrint = typeof element === 'string' ? document.getElementById(element) : element;

  if (!elementToPrint) {
    console.error('Element not found');
    return;
  }

  printWindow.document.write(`
    <html>
      <head>
        <title>Yazdır</title>
        <style>
          body { font-family: Arial, sans-serif; }
          @media print {
            @page { margin: 1cm; }
            body { margin: 0; }
          }
        </style>
      </head>
      <body>
        ${elementToPrint.innerHTML}
      </body>
    </html>
  `);

  printWindow.document.close();
  printWindow.focus();
  printWindow.print();
  printWindow.close();
};

/**
 * PDF olarak indir (basit versiyon - daha gelişmiş için jsPDF kullanılabilir)
 */
export const downloadAsPDF = (element, filename = 'document.pdf') => {
  // TODO: jsPDF entegrasyonu
  console.log('PDF download not yet implemented. Use printElement for now.');
  printElement(element);
};

/**
 * Excel olarak export (basit CSV)
 */
export const exportToExcel = (data, filename = 'data.xlsx') => {
  // TODO: xlsx kütüphanesi entegrasyonu
  if (!data || data.length === 0) return;

  const headers = Object.keys(data[0]);
  const csvContent = [
    headers.join(','),
    ...data.map((row) => headers.map((header) => row[header] || '').join(',')),
  ].join('\n');

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = filename.replace('.xlsx', '.csv');
  link.click();
};

