package com.healthtourism.reminder.repository;

import com.healthtourism.reminder.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByStatusAndScheduledAtLessThanEqual(Reminder.ReminderStatus status, LocalDateTime now);
    List<Reminder> findByReminderTypeAndEntityId(Reminder.ReminderType reminderType, Long entityId);
    List<Reminder> findByUserIdOrderByScheduledAtDesc(Long userId);
    List<Reminder> findByReminderTypeAndStatus(Reminder.ReminderType reminderType, Reminder.ReminderStatus status);
    
    @Query("SELECT r FROM Reminder r WHERE r.status = 'PENDING' AND r.scheduledAt <= :now")
    List<Reminder> findPendingRemindersToSend(LocalDateTime now);
}
