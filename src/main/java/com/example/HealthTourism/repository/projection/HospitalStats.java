package com.example.HealthTourism.repository.projection;

/**
 * Interface-based projection for hospital statistics.
 * Used for analytics and reporting in management dashboard.
 * 
 * Veri analitiği için kullanılan projection interface.
 * Kurumsal yönetim panelinde hangi şehirlerde yoğunluk olduğunu
 * veya hangi hastanelerin daha çok tercih edildiğini anlamak için kullanılır.
 * 
 * Usage:
 * <pre>
 * List&lt;HospitalStats&gt; stats = repository.getHospitalStatsByCity();
 * stats.forEach(stat -&gt; {
 *     System.out.println(stat.getCity());
 *     System.out.println(stat.getHospitalCount());
 *     System.out.println(stat.getAverageRating());
 * });
 * </pre>
 */
public interface HospitalStats {
    
    /**
     * Şehir adı
     */
    String getCity();
    
    /**
     * O şehirdeki toplam hastane sayısı
     */
    Long getHospitalCount();
    
    /**
     * O şehirdeki hastanelerin ortalama puanı
     */
    Double getAverageRating();
}

