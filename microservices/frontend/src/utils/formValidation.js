// src/utils/formValidation.js
import * as yup from 'yup';

export const validationSchemas = {
  email: yup
    .string()
    .email('Geçerli bir e-posta adresi girin')
    .required('E-posta adresi gereklidir'),

  password: yup
    .string()
    .min(6, 'Şifre en az 6 karakter olmalıdır')
    .matches(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      'Şifre en az bir büyük harf, bir küçük harf ve bir rakam içermelidir'
    )
    .required('Şifre gereklidir'),

  phone: yup
    .string()
    .matches(/^[0-9]{10,15}$/, 'Geçerli bir telefon numarası girin')
    .required('Telefon numarası gereklidir'),

  required: (fieldName) => yup.string().required(`${fieldName} gereklidir`),

  date: yup
    .date()
    .required('Tarih gereklidir')
    .min(new Date(), 'Geçmiş bir tarih seçilemez'),

  number: (min, max) => yup
    .number()
    .min(min, `Minimum değer ${min} olmalıdır`)
    .max(max, `Maksimum değer ${max} olmalıdır`)
    .required('Sayı gereklidir'),
};

export const validateForm = async (schema, data) => {
  try {
    await schema.validate(data, { abortEarly: false });
    return { isValid: true, errors: {} };
  } catch (error) {
    const errors = {};
    error.inner.forEach((err) => {
      errors[err.path] = err.message;
    });
    return { isValid: false, errors };
  }
};

