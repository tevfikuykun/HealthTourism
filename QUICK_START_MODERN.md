# 🚀 Modern Özellikler - Hızlı Başlangıç

## 📦 Yeni Eklenen Kütüphaneler

### Frontend Kurulumu
```bash
cd microservices/frontend
npm install
```

### Backend Kurulumu
Maven otomatik olarak bağımlılıkları indirecektir:
```bash
cd microservices/auth-service
mvn clean install
```

## 🎯 Hızlı Kullanım Örnekleri

### Frontend - React Query ile Data Fetching
```javascript
import { useQuery } from '@tanstack/react-query';
import { hospitalService } from '../services/api';

function Hospitals() {
  const { data, isLoading, error } = useQuery({
    queryKey: ['hospitals'],
    queryFn: () => hospitalService.getAll(),
  });
  
  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;
  
  return <div>{/* Render hospitals */}</div>;
}
```

### Frontend - Form Validation
```javascript
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';

const schema = yup.object({
  email: yup.string().email('Invalid email').required('Email is required'),
  password: yup.string().min(8, 'Min 8 characters').required('Password is required'),
});

function LoginForm() {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });
  
  const onSubmit = (data) => {
    console.log(data);
  };
  
  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('email')} />
      {errors.email && <span>{errors.email.message}</span>}
      
      <input type="password" {...register('password')} />
      {errors.password && <span>{errors.password.message}</span>}
      
      <button type="submit">Login</button>
    </form>
  );
}
```

### Frontend - Error Handling
```javascript
import { toast } from 'react-toastify';
import { handleApiError } from '../utils/errorHandler';

try {
  await api.post('/endpoint', data);
  toast.success('Success!');
} catch (error) {
  const appError = handleApiError(error);
  toast.error(appError.message);
}
```

### Backend - Circuit Breaker
```java
@Service
public class MyService {
    
    @CircuitBreaker(name = "myService", fallbackMethod = "fallback")
    @Retry(name = "myService")
    public String callExternalService() {
        // Your service call that might fail
        return externalApi.call();
    }
    
    public String fallback(Exception ex) {
        logger.error("Service call failed, using fallback", ex);
        return "Fallback response";
    }
}
```

### Backend - Retry Mechanism
```java
@Service
public class MyService {
    
    @Retry(name = "myService", fallbackMethod = "fallback")
    public String unreliableOperation() {
        // Operation that might fail but should be retried
        return someOperation();
    }
}
```

## 🔧 Yapılandırma

### Frontend Environment Variables
`.env` dosyası oluşturun:
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Health Tourism
VITE_ENABLE_PWA=true
```

### Backend Resilience Configuration
`application.properties` içinde:
```properties
# Circuit Breaker
resilience4j.circuitbreaker.instances.myService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.myService.wait-duration-in-open-state=30s

# Retry
resilience4j.retry.instances.myService.max-attempts=3
resilience4j.retry.instances.myService.wait-duration=500ms
```

## 📚 Daha Fazla Bilgi

Detaylı dokümantasyon için `MODERN_LIBRARIES_ADDED.md` dosyasına bakın.

