// Error handling utilities
export class AppError extends Error {
  constructor(message, statusCode, code) {
    super(message);
    this.name = 'AppError';
    this.statusCode = statusCode;
    this.code = code;
  }
}

export const handleApiError = (error) => {
  if (error.response) {
    // Server responded with error
    const { status, data } = error.response;
    switch (status) {
      case 400:
        // Show detailed validation errors if available
        let errorMessage = data.message || 'Invalid request';
        if (data.errors && Array.isArray(data.errors)) {
          errorMessage = data.errors.map(e => e.defaultMessage || e.message).join(', ');
        } else if (data.error) {
          errorMessage = data.error;
        } else if (typeof data === 'string') {
          errorMessage = data;
        }
        return new AppError(errorMessage, 400, 'BAD_REQUEST');
      case 401:
        return new AppError('Unauthorized. Please login again.', 401, 'UNAUTHORIZED');
      case 403:
        return new AppError('You do not have permission to perform this action.', 403, 'FORBIDDEN');
      case 404:
        return new AppError('Resource not found.', 404, 'NOT_FOUND');
      case 429:
        return new AppError('Too many requests. Please try again later.', 429, 'RATE_LIMIT');
      case 500:
        return new AppError('Server error. Please try again later.', 500, 'SERVER_ERROR');
      case 503:
        return new AppError('Service unavailable. Please try again later.', 503, 'SERVICE_UNAVAILABLE');
      default:
        return new AppError(data.message || 'An error occurred', status, 'UNKNOWN');
    }
  } else if (error.request) {
    // Request made but no response
    return new AppError('Backend servisine bağlanılamadı. Lütfen backend servislerinin çalıştığından emin olun.', 0, 'NETWORK_ERROR');
  } else {
    // Something else happened
    return new AppError(error.message || 'An unexpected error occurred', 0, 'UNKNOWN');
  }
};

export const getErrorMessage = (error) => {
  if (error instanceof AppError) {
    return error.message;
  }
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  if (error.message) {
    return error.message;
  }
  return 'An unexpected error occurred';
};

