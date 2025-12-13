import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authService } from '../services/api';
import { toast } from 'react-toastify';

export function useAuth() {
  const queryClient = useQueryClient();

  const loginMutation = useMutation({
    mutationFn: authService.login,
    onSuccess: (data) => {
      localStorage.setItem('token', data.data.accessToken);
      localStorage.setItem('refreshToken', data.data.refreshToken);
      localStorage.setItem('user', JSON.stringify(data.data));
      queryClient.setQueryData(['user'], data.data);
      toast.success('Login successful!');
    },
    onError: (error) => {
      toast.error(error.message || 'Login failed');
    },
  });

  const registerMutation = useMutation({
    mutationFn: authService.register,
    onSuccess: (data) => {
      localStorage.setItem('token', data.data.accessToken);
      localStorage.setItem('refreshToken', data.data.refreshToken);
      localStorage.setItem('user', JSON.stringify(data.data));
      queryClient.setQueryData(['user'], data.data);
      toast.success('Registration successful! Please verify your email.');
    },
    onError: (error) => {
      toast.error(error.message || 'Registration failed');
    },
  });

  const logoutMutation = useMutation({
    mutationFn: () => {
      const refreshToken = localStorage.getItem('refreshToken');
      return authService.logout(refreshToken);
    },
    onSuccess: () => {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      queryClient.clear();
      toast.success('Logged out successfully');
    },
    onError: (error) => {
      // Even if logout fails, clear local storage
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      queryClient.clear();
    },
  });

  const { data: user, isLoading: isUserLoading } = useQuery({
    queryKey: ['user'],
    queryFn: () => {
      const userStr = localStorage.getItem('user');
      return userStr ? JSON.parse(userStr) : null;
    },
    staleTime: Infinity,
  });

  const isAuthenticated = !!user && !!localStorage.getItem('token');
  const isLoading = isUserLoading || loginMutation.isPending || registerMutation.isPending;

  return {
    user,
    isAuthenticated,
    isLoading,
    login: loginMutation.mutate,
    register: registerMutation.mutate,
    logout: logoutMutation.mutate,
  };
}

