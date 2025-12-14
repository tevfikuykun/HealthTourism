package com.example.HealthTourism.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Scheduled tasks için örnek sınıf
 * Bu sınıf, periyodik olarak çalışacak görevleri içerir
 */
@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Her 5 dakikada bir sistem durumunu logla
     * fixedRate: Sabit aralıklarla çalışır (önceki görev bitmesini beklemez)
     */
    @Scheduled(fixedRate = 300000) // 5 dakika = 300000 ms
    public void reportSystemStatus() {
        logger.info("Sistem Durum Kontrolü: {} - Sistem çalışıyor", 
            LocalDateTime.now().format(dateTimeFormatter));
    }

    /**
     * Her gün saat 02:00'de çalışacak görev (örnek: veritabanı temizliği, rapor oluşturma)
     * cron formatı: saniye dakika saat gün ay haftanın günü
     */
    @Scheduled(cron = "0 0 2 * * ?") // Her gün saat 02:00
    public void performDailyMaintenance() {
        logger.info("Günlük Bakım Görevi Başlatıldı: {}", 
            LocalDateTime.now().format(dateTimeFormatter));
        // Buraya günlük bakım işlemleri eklenebilir:
        // - Eski log kayıtlarını temizleme
        // - Veritabanı optimizasyonu
        // - Rapor oluşturma
        // - Cache temizleme
    }

    /**
     * Her saat başı çalışacak görev (örnek: rezervasyon kontrolü)
     */
    @Scheduled(cron = "0 0 * * * ?") // Her saat başı
    public void checkReservations() {
        logger.debug("Rezervasyon Kontrolü: {}", 
            LocalDateTime.now().format(dateTimeFormatter));
        // Buraya rezervasyon kontrol işlemleri eklenebilir:
        // - Yaklaşan randevuları kontrol et
        // - Süresi dolmuş rezervasyonları güncelle
        // - Bildirim gönder
    }
}

