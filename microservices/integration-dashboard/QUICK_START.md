# 🚀 Integration Dashboard - Hızlı Başlatma

## ⚠️ Maven Gerekli

Integration Dashboard'u çalıştırmak için Maven yüklü olmalı.

## 📥 Maven Kurulumu

### Windows için:

1. **Maven İndir:**
   - https://maven.apache.org/download.cgi
   - `apache-maven-3.9.x-bin.zip` indir

2. **Kur:**
   - ZIP'i aç (örn: `C:\Program Files\Apache\maven`)
   - Environment Variables'a ekle:
     - `MAVEN_HOME` = `C:\Program Files\Apache\maven`
     - `PATH`'e ekle: `%MAVEN_HOME%\bin`

3. **Kontrol Et:**
   ```cmd
   mvn --version
   ```

## 🚀 Alternatif: Maven Wrapper Kullan

Maven yüklü değilse, Maven Wrapper kullanabilirsiniz:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

## ✅ Servisi Başlat

Maven kurulduktan sonra:

```bash
cd microservices/integration-dashboard
mvn spring-boot:run
```

Veya script ile:
```bash
scripts\start-integration-dashboard.bat
```

## 🌐 Erişim

```
http://localhost:3002
```

---

## 🔄 Hızlı Çözüm: Basit HTML Dashboard

Maven kurulumu zaman alıyorsa, basit bir HTML dashboard oluşturabilirim. İster misiniz?


