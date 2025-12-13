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
        return new AppError(data.message || 'Invalid request', 400, 'BAD_REQUEST');
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
      default:
        return new AppError(data.message || 'An error occurred', status, 'UNKNOWN');
    }
  } else if (error.request) {
    // Request made but no response
    return new AppError('Network error. Please check your connection.', 0, 'NETWORK_ERROR');
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

