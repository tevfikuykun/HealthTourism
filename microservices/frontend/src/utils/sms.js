// src/utils/sms.js

export const sendSMS = async (phoneNumber, message) => {
  // Backend API'ye gönderilecek
  const response = await fetch('/api/sms/send', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      phoneNumber,
      message,
    }),
  });
  return response.json();
};

export const sendVerificationCode = async (phoneNumber) => {
  const code = Math.floor(100000 + Math.random() * 900000).toString();
  await sendSMS(phoneNumber, `Doğrulama kodunuz: ${code}`);
  return code;
};

export const sendAppointmentReminder = async (phoneNumber, appointment) => {
  const message = `Randevu Hatırlatması: ${appointment.date} ${appointment.time} - ${appointment.doctor} - ${appointment.hospital}`;
  return sendSMS(phoneNumber, message);
};

