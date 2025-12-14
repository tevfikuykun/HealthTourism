// src/utils/emailTemplates.js

export const emailTemplates = {
  welcome: (userName) => ({
    subject: 'Health Tourism\'a Hoş Geldiniz!',
    html: `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h1 style="color: #1976d2;">Hoş Geldiniz, ${userName}!</h1>
        <p>Health Tourism ailesine katıldığınız için teşekkür ederiz.</p>
        <p>Hesabınız başarıyla oluşturuldu. Artık tüm hizmetlerimizden yararlanabilirsiniz.</p>
        <a href="${process.env.VITE_APP_URL || 'http://localhost:3000'}" 
           style="display: inline-block; padding: 10px 20px; background-color: #1976d2; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px;">
          Hemen Başla
        </a>
      </div>
    `,
  }),

  reservationConfirmation: (reservation) => ({
    subject: 'Rezervasyon Onayı',
    html: `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h1 style="color: #1976d2;">Rezervasyonunuz Onaylandı</h1>
        <p>Sayın ${reservation.userName},</p>
        <p>Rezervasyonunuz başarıyla oluşturuldu:</p>
        <ul>
          <li><strong>Rezervasyon No:</strong> ${reservation.id}</li>
          <li><strong>Tarih:</strong> ${reservation.date}</li>
          <li><strong>Saat:</strong> ${reservation.time}</li>
          <li><strong>Hizmet:</strong> ${reservation.service}</li>
        </ul>
        <p>Rezervasyon detaylarınızı görmek için lütfen hesabınıza giriş yapın.</p>
      </div>
    `,
  }),

  paymentReceipt: (payment) => ({
    subject: 'Ödeme Makbuzu',
    html: `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h1 style="color: #1976d2;">Ödeme Makbuzu</h1>
        <p>Ödemeniz başarıyla alınmıştır.</p>
        <table style="width: 100%; border-collapse: collapse; margin: 20px 0;">
          <tr>
            <td style="padding: 10px; border: 1px solid #ddd;"><strong>Ödeme No:</strong></td>
            <td style="padding: 10px; border: 1px solid #ddd;">${payment.id}</td>
          </tr>
          <tr>
            <td style="padding: 10px; border: 1px solid #ddd;"><strong>Tutar:</strong></td>
            <td style="padding: 10px; border: 1px solid #ddd;">₺${payment.amount}</td>
          </tr>
          <tr>
            <td style="padding: 10px; border: 1px solid #ddd;"><strong>Tarih:</strong></td>
            <td style="padding: 10px; border: 1px solid #ddd;">${payment.date}</td>
          </tr>
        </table>
        <p>Faturanızı indirmek için lütfen hesabınıza giriş yapın.</p>
      </div>
    `,
  }),

  appointmentReminder: (appointment) => ({
    subject: 'Randevu Hatırlatması',
    html: `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h1 style="color: #1976d2;">Randevu Hatırlatması</h1>
        <p>Sayın ${appointment.userName},</p>
        <p>Yarın saat ${appointment.time} için randevunuz bulunmaktadır.</p>
        <p><strong>Doktor:</strong> ${appointment.doctor}</p>
        <p><strong>Hastane:</strong> ${appointment.hospital}</p>
        <p>Lütfen randevu saatinden 15 dakika önce hazır bulunun.</p>
      </div>
    `,
  }),
};

export const sendEmail = async (to, template, data) => {
  // Backend API'ye gönderilecek
  const response = await fetch('/api/email/send', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      to,
      template: template(data),
    }),
  });
  return response.json();
};

