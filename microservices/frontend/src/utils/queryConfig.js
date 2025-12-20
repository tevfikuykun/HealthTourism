// Utility function for polling queries that stop on network errors
export const createPollingQueryConfig = (interval, options = {}) => {
  return {
    refetchInterval: (query) => {
      // Stop polling if there's a network error
      if (query?.state?.error?.code === 'NETWORK_ERROR' || 
          query?.state?.error?.statusCode === 0 ||
          query?.state?.error?.message?.includes('Network error')) {
        return false;
      }
      return interval;
    },
    retry: false, // Don't retry on error to prevent spam
    refetchOnWindowFocus: false, // Don't refetch on window focus
    refetchOnReconnect: false, // Don't refetch on reconnect
    ...options,
  };
};

// Default query config for all queries
export const defaultQueryConfig = {
  retry: (failureCount, error) => {
    // Don't retry on network errors
    if (error?.code === 'NETWORK_ERROR' || error?.statusCode === 0) {
      return false;
    }
    return failureCount < 1;
  },
  retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
  refetchOnWindowFocus: false,
  refetchOnReconnect: false,
  staleTime: 5 * 60 * 1000, // 5 minutes
};



