package com.healthtourism.reminder.service;

import com.healthtourism.reminder.entity.Reminder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Message Personalization Service
 * Creates personalized reminder messages with dynamic content
 * Supports multi-language templates (Turkish, English, Arabic, German)
 */
@Service
public class MessagePersonalizationService {
    
    @Value("${reminder.default.language:tr}")
    private String defaultLanguage;
    
    /**
     * Generate personalized message based on reminder type and user data
     * Supports multi-language templates
     */
    public String generatePersonalizedMessage(
            Reminder.ReminderType reminderType,
            String recipientName,
            String treatmentType,
            String quoteNumber,
            String abTestVariant) {
        return generatePersonalizedMessage(reminderType, recipientName, treatmentType, quoteNumber, abTestVariant, defaultLanguage);
    }
    
    /**
     * Generate personalized message with language support
     */
    public String generatePersonalizedMessage(
            Reminder.ReminderType reminderType,
            String recipientName,
            String treatmentType,
            String quoteNumber,
            String abTestVariant,
            String language) {
        
        // Validate language, fallback to default
        if (language == null || language.isEmpty()) {
            language = defaultLanguage;
        }
        
        Map<String, String> variables = new HashMap<>();
        variables.put("recipientName", recipientName != null ? recipientName : getDefaultRecipientName(language));
        variables.put("treatmentType", treatmentType != null ? treatmentType : getDefaultTreatmentType(language));
        variables.put("quoteNumber", quoteNumber != null ? quoteNumber : "");
        
        switch (reminderType) {
            case QUOTE_PENDING:
                return generateQuotePendingMessage(variables, abTestVariant, language);
            case QUOTE_EXPIRING:
                return generateQuoteExpiringMessage(variables, abTestVariant, language);
            case LEAD_FOLLOW_UP:
                return generateLeadFollowUpMessage(variables, language);
            default:
                return getDefaultMessage(language);
        }
    }
    
    private String generateQuotePendingMessage(Map<String, String> variables, String variant, String language) {
        String name = variables.get("recipientName");
        String treatment = variables.get("treatmentType");
        
        Map<String, Map<String, String>> templates = getQuotePendingTemplates();
        Map<String, String> langTemplates = templates.getOrDefault(language, templates.get(defaultLanguage));
        
        String template = "B".equals(variant) ? langTemplates.get("variantB") : langTemplates.get("variantA");
        
        return String.format(template, name, treatment);
    }
    
    private String generateQuoteExpiringMessage(Map<String, String> variables, String variant, String language) {
        String name = variables.get("recipientName");
        String treatment = variables.get("treatmentType");
        String quoteNumber = variables.get("quoteNumber");
        
        Map<String, Map<String, String>> templates = getQuoteExpiringTemplates();
        Map<String, String> langTemplates = templates.getOrDefault(language, templates.get(defaultLanguage));
        
        String template = "B".equals(variant) ? langTemplates.get("variantB") : langTemplates.get("variantA");
        
        return String.format(template, name, treatment, quoteNumber);
    }
    
    private String generateLeadFollowUpMessage(Map<String, String> variables, String language) {
        String name = variables.get("recipientName");
        
        Map<String, String> templates = getLeadFollowUpTemplates();
        String template = templates.getOrDefault(language, templates.get(defaultLanguage));
        
        return String.format(template, name);
    }
    
    /**
     * Get quote pending message templates by language
     */
    private Map<String, Map<String, String>> getQuotePendingTemplates() {
        Map<String, Map<String, String>> templates = new HashMap<>();
        
        // Turkish
        Map<String, String> tr = new HashMap<>();
        tr.put("variantA", "Sayın %s, %s teklifinizi değerlendirmek için bekliyoruz. Sorularınız için her zaman buradayız.");
        tr.put("variantB", "Sayın %s, %s teklifinizi değerlendirmek için bekliyoruz. Hala ilgileniyor musunuz? Size özel hazırladığımız bu teklifi kaçırmayın!");
        templates.put("tr", tr);
        
        // English
        Map<String, String> en = new HashMap<>();
        en.put("variantA", "Dear %s, we are waiting for you to review your %s quote. We are always here for your questions.");
        en.put("variantB", "Dear %s, we are waiting for you to review your %s quote. Are you still interested? Don't miss this special offer we've prepared for you!");
        templates.put("en", en);
        
        // Arabic
        Map<String, String> ar = new HashMap<>();
        ar.put("variantA", "عزيزي %s، نحن في انتظارك لمراجعة عرض %s الخاص بك. نحن دائماً هنا للإجابة على أسئلتك.");
        ar.put("variantB", "عزيزي %s، نحن في انتظارك لمراجعة عرض %s الخاص بك. هل ما زلت مهتماً؟ لا تفوت هذه العروض الخاصة التي أعددناها لك!");
        templates.put("ar", ar);
        
        // German
        Map<String, String> de = new HashMap<>();
        de.put("variantA", "Sehr geehrter %s, wir warten darauf, dass Sie Ihr %s-Angebot prüfen. Wir sind immer für Ihre Fragen da.");
        de.put("variantB", "Sehr geehrter %s, wir warten darauf, dass Sie Ihr %s-Angebot prüfen. Sind Sie noch interessiert? Verpassen Sie nicht dieses spezielle Angebot, das wir für Sie vorbereitet haben!");
        templates.put("de", de);
        
        return templates;
    }
    
    /**
     * Get quote expiring message templates by language
     */
    private Map<String, Map<String, String>> getQuoteExpiringTemplates() {
        Map<String, Map<String, String>> templates = new HashMap<>();
        
        // Turkish
        Map<String, String> tr = new HashMap<>();
        tr.put("variantA", "Sayın %s, %s teklifinizin (No: %s) süresi yakında dolacak. Değerlendirmek için zamanınız kalmışken bize ulaşabilirsiniz.");
        tr.put("variantB", "Sayın %s, %s teklifinizin (No: %s) süresi yakında dolacak! Hemen değerlendirin ve özel fiyat avantajından yararlanın.");
        templates.put("tr", tr);
        
        // English
        Map<String, String> en = new HashMap<>();
        en.put("variantA", "Dear %s, your %s quote (No: %s) will expire soon. You can contact us while you still have time to review.");
        en.put("variantB", "Dear %s, your %s quote (No: %s) will expire soon! Review it immediately and take advantage of the special price.");
        templates.put("en", en);
        
        // Arabic
        Map<String, String> ar = new HashMap<>();
        ar.put("variantA", "عزيزي %s، ستنتهي صلاحية عرض %s الخاص بك (رقم: %s) قريباً. يمكنك الاتصال بنا بينما لا يزال لديك وقت للمراجعة.");
        ar.put("variantB", "عزيزي %s، ستنتهي صلاحية عرض %s الخاص بك (رقم: %s) قريباً! راجعه فوراً واستفد من السعر الخاص.");
        templates.put("ar", ar);
        
        // German
        Map<String, String> de = new HashMap<>();
        de.put("variantA", "Sehr geehrter %s, Ihr %s-Angebot (Nr: %s) läuft bald ab. Sie können uns kontaktieren, solange Sie noch Zeit zur Prüfung haben.");
        de.put("variantB", "Sehr geehrter %s, Ihr %s-Angebot (Nr: %s) läuft bald ab! Prüfen Sie es sofort und nutzen Sie den Sonderpreis.");
        templates.put("de", de);
        
        return templates;
    }
    
    /**
     * Get lead follow-up message templates by language
     */
    private Map<String, String> getLeadFollowUpTemplates() {
        Map<String, String> templates = new HashMap<>();
        templates.put("tr", "Sayın %s, size nasıl yardımcı olabiliriz? Sağlık yolculuğunuz için sorularınız varsa, bizimle iletişime geçmekten çekinmeyin.");
        templates.put("en", "Dear %s, how can we help you? If you have any questions about your health journey, please don't hesitate to contact us.");
        templates.put("ar", "عزيزي %s، كيف يمكننا مساعدتك؟ إذا كان لديك أي أسئلة حول رحلتك الصحية، لا تتردد في الاتصال بنا.");
        templates.put("de", "Sehr geehrter %s, wie können wir Ihnen helfen? Wenn Sie Fragen zu Ihrer Gesundheitsreise haben, zögern Sie bitte nicht, uns zu kontaktieren.");
        return templates;
    }
    
    /**
     * Get default recipient name by language
     */
    private String getDefaultRecipientName(String language) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("tr", "Değerli Müşterimiz");
        defaults.put("en", "Dear Customer");
        defaults.put("ar", "عميلنا العزيز");
        defaults.put("de", "Lieber Kunde");
        return defaults.getOrDefault(language, defaults.get(defaultLanguage));
    }
    
    /**
     * Get default treatment type by language
     */
    private String getDefaultTreatmentType(String language) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("tr", "tedavi");
        defaults.put("en", "treatment");
        defaults.put("ar", "العلاج");
        defaults.put("de", "Behandlung");
        return defaults.getOrDefault(language, defaults.get(defaultLanguage));
    }
    
    /**
     * Get default message by language
     */
    private String getDefaultMessage(String language) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("tr", "Size nasıl yardımcı olabiliriz?");
        defaults.put("en", "How can we help you?");
        defaults.put("ar", "كيف يمكننا مساعدتك؟");
        defaults.put("de", "Wie können wir Ihnen helfen?");
        return defaults.getOrDefault(language, defaults.get(defaultLanguage));
    }
    
    /**
     * Generate email subject with personalization
     */
    public String generateEmailSubject(Reminder.ReminderType reminderType, String recipientName, String treatmentType) {
        return generateEmailSubject(reminderType, recipientName, treatmentType, defaultLanguage);
    }
    
    /**
     * Generate email subject with language support
     */
    public String generateEmailSubject(Reminder.ReminderType reminderType, String recipientName, String treatmentType, String language) {
        if (language == null || language.isEmpty()) {
            language = defaultLanguage;
        }
        
        String name = recipientName != null ? recipientName.split(" ")[0] : getDefaultRecipientName(language);
        
        Map<String, Map<Reminder.ReminderType, String>> subjects = getEmailSubjectTemplates();
        Map<Reminder.ReminderType, String> langSubjects = subjects.getOrDefault(language, subjects.get(defaultLanguage));
        
        return String.format(langSubjects.getOrDefault(reminderType, langSubjects.get(Reminder.ReminderType.LEAD_FOLLOW_UP)), name);
    }
    
    /**
     * Get email subject templates by language
     */
    private Map<String, Map<Reminder.ReminderType, String>> getEmailSubjectTemplates() {
        Map<String, Map<Reminder.ReminderType, String>> templates = new HashMap<>();
        
        // Turkish
        Map<Reminder.ReminderType, String> tr = new HashMap<>();
        tr.put(Reminder.ReminderType.QUOTE_PENDING, "%s Bey/Hanım, Teklifinizi Değerlendirmeyi Unutmayın");
        tr.put(Reminder.ReminderType.QUOTE_EXPIRING, "%s Bey/Hanım, Teklifinizin Süresi Dolmak Üzere");
        tr.put(Reminder.ReminderType.LEAD_FOLLOW_UP, "%s Bey/Hanım, Size Nasıl Yardımcı Olabiliriz?");
        templates.put("tr", tr);
        
        // English
        Map<Reminder.ReminderType, String> en = new HashMap<>();
        en.put(Reminder.ReminderType.QUOTE_PENDING, "Dear %s, Don't Forget to Review Your Quote");
        en.put(Reminder.ReminderType.QUOTE_EXPIRING, "Dear %s, Your Quote is About to Expire");
        en.put(Reminder.ReminderType.LEAD_FOLLOW_UP, "Dear %s, How Can We Help You?");
        templates.put("en", en);
        
        // Arabic
        Map<Reminder.ReminderType, String> ar = new HashMap<>();
        ar.put(Reminder.ReminderType.QUOTE_PENDING, "عزيزي %s، لا تنس مراجعة عرضك");
        ar.put(Reminder.ReminderType.QUOTE_EXPIRING, "عزيزي %s، عرضك على وشك الانتهاء");
        ar.put(Reminder.ReminderType.LEAD_FOLLOW_UP, "عزيزي %s، كيف يمكننا مساعدتك؟");
        templates.put("ar", ar);
        
        // German
        Map<Reminder.ReminderType, String> de = new HashMap<>();
        de.put(Reminder.ReminderType.QUOTE_PENDING, "Sehr geehrter %s, vergessen Sie nicht, Ihr Angebot zu prüfen");
        de.put(Reminder.ReminderType.QUOTE_EXPIRING, "Sehr geehrter %s, Ihr Angebot läuft bald ab");
        de.put(Reminder.ReminderType.LEAD_FOLLOW_UP, "Sehr geehrter %s, wie können wir Ihnen helfen?");
        templates.put("de", de);
        
        return templates;
    }
}
