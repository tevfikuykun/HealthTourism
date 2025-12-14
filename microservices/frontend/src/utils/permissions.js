// src/utils/permissions.js

// Check if user has permission
export const hasPermission = (user, permission) => {
  if (!user || !user.role || !user.permissions) return false;
  
  // Admin has all permissions
  if (user.role === 'ADMIN') return true;
  
  return user.permissions.includes(permission);
};

// Check if user has role
export const hasRole = (user, role) => {
  if (!user || !user.role) return false;
  return user.role === role;
};

// Check if user has any of the roles
export const hasAnyRole = (user, roles) => {
  if (!user || !user.role) return false;
  return roles.includes(user.role);
};

// Check if user can access resource
export const canAccess = (user, resource, action) => {
  if (!user) return false;
  
  // Admin can access everything
  if (user.role === 'ADMIN') return true;
  
  // Check specific permission
  const permission = `${resource}:${action}`;
  return hasPermission(user, permission);
};

// User roles
export const ROLES = {
  ADMIN: 'ADMIN',
  USER: 'USER',
  DOCTOR: 'DOCTOR',
  HOSPITAL_ADMIN: 'HOSPITAL_ADMIN',
};

// Common permissions
export const PERMISSIONS = {
  // Users
  USER_VIEW: 'user:view',
  USER_CREATE: 'user:create',
  USER_UPDATE: 'user:update',
  USER_DELETE: 'user:delete',
  
  // Reservations
  RESERVATION_VIEW: 'reservation:view',
  RESERVATION_CREATE: 'reservation:create',
  RESERVATION_UPDATE: 'reservation:update',
  RESERVATION_DELETE: 'reservation:delete',
  
  // Payments
  PAYMENT_VIEW: 'payment:view',
  PAYMENT_CREATE: 'payment:create',
  
  // Admin
  ADMIN_ACCESS: 'admin:access',
};

