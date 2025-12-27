import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { Calendar, Clock, AlertCircle, CheckCircle2 } from 'lucide-react';
import { reservationService } from '../../services/api';
import { cn } from '../../lib/utils';
import { Skeleton } from '../ui/Skeleton';
import { Button } from '../ui/Button';

/**
 * Availability Calendar Component
 * Backend'deki ReservationConflictException mekanizmasıyla entegre
 * Rezervasyon çakışmalarını önceden engelleyen akıllı takvim
 * 
 * @param {object} doctor - Seçilen doktor
 * @param {object} hospital - Seçilen hastane
 * @param {function} onDateSelect - Tarih seçildiğinde çağrılır
 * @param {function} onTimeSelect - Saat seçildiğinde çağrılır
 * @param {string} selectedDate - Seçili tarih
 * @param {string} selectedTime - Seçili saat
 */
export const AvailabilityCalendar = ({
  doctor,
  hospital,
  onDateSelect,
  onTimeSelect,
  selectedDate,
  selectedTime,
}) => {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [availableSlots, setAvailableSlots] = useState([]);
  const [conflicts, setConflicts] = useState(new Set());

  // Mevcut ayın tarihlerini al
  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    const days = [];
    
    // Önceki ayın son günleri (boş hücreler)
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }
    
    // Mevcut ayın günleri
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(year, month, day));
    }
    
    return days;
  };

  // Seçili tarih için müsait saatleri çek
  const { data: slotsData, isLoading: slotsLoading } = useQuery({
    queryKey: ['available-slots', doctor?.id, hospital?.id, selectedDate],
    queryFn: () => {
      if (!doctor?.id || !hospital?.id || !selectedDate) {
        return Promise.resolve({ data: [] });
      }
      return reservationService.getAvailableSlots(
        doctor.id,
        hospital.id,
        selectedDate
      );
    },
    enabled: !!doctor?.id && !!hospital?.id && !!selectedDate,
  });

  useEffect(() => {
    if (slotsData?.data) {
      setAvailableSlots(slotsData.data);
    }
  }, [slotsData]);

  // Tarih seçildiğinde çakışma kontrolü yap
  const handleDateClick = async (date) => {
    if (!date) return;
    
    const dateString = date.toISOString().split('T')[0];
    onDateSelect(dateString);
    
    // Çakışma kontrolü
    if (doctor?.id && hospital?.id) {
      try {
        const conflictCheck = await reservationService.checkConflict({
          doctorId: doctor.id,
          hospitalId: hospital.id,
          date: dateString,
        });
        
        if (conflictCheck.data?.hasConflict) {
          setConflicts((prev) => new Set([...prev, dateString]));
        } else {
          setConflicts((prev) => {
            const newSet = new Set(prev);
            newSet.delete(dateString);
            return newSet;
          });
        }
      } catch (error) {
        console.error('Conflict check error:', error);
      }
    }
  };

  // Tarih formatı
  const formatDate = (date) => {
    if (!date) return '';
    return date.toLocaleDateString('tr-TR', { 
      day: 'numeric', 
      month: 'short' 
    });
  };

  // Tarih geçmişte mi?
  const isPastDate = (date) => {
    if (!date) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
  };

  // Tarih bugün mü?
  const isToday = (date) => {
    if (!date) return false;
    const today = new Date();
    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    );
  };

  // Tarih seçili mi?
  const isSelected = (date) => {
    if (!date || !selectedDate) return false;
    return date.toISOString().split('T')[0] === selectedDate;
  };

  // Tarih çakışma var mı?
  const hasConflict = (date) => {
    if (!date) return false;
    const dateString = date.toISOString().split('T')[0];
    return conflicts.has(dateString);
  };

  const days = getDaysInMonth(currentMonth);
  const timeSlots = ['09:00', '10:00', '11:00', '13:00', '14:00', '15:00', '16:00', '17:00'];

  const nextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1));
  };

  const prevMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1));
  };

  return (
    <div className="space-y-6">
      {/* Calendar Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Calendar className="h-5 w-5 text-primary-600" />
          <h3 className="text-lg font-semibold">Randevu Tarihi Seçin</h3>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={prevMonth}
          >
            ←
          </Button>
          <span className="min-w-[120px] text-center font-medium">
            {currentMonth.toLocaleDateString('tr-TR', { 
              month: 'long', 
              year: 'numeric' 
            })}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={nextMonth}
          >
            →
          </Button>
        </div>
      </div>

      {/* Calendar Grid */}
      <div className="rounded-lg border border-slate-200 bg-white p-4">
        {/* Weekday Headers */}
        <div className="mb-2 grid grid-cols-7 gap-1">
          {['Pzt', 'Sal', 'Çar', 'Per', 'Cum', 'Cmt', 'Paz'].map((day) => (
            <div
              key={day}
              className="p-2 text-center text-xs font-semibold text-slate-500"
            >
              {day}
            </div>
          ))}
        </div>

        {/* Calendar Days */}
        <div className="grid grid-cols-7 gap-1">
          {days.map((date, index) => {
            if (!date) {
              return <div key={`empty-${index}`} className="p-2" />;
            }

            const past = isPastDate(date);
            const today = isToday(date);
            const selected = isSelected(date);
            const conflict = hasConflict(date);

            return (
              <motion.button
                key={date.toISOString()}
                onClick={() => !past && !conflict && handleDateClick(date)}
                disabled={past || conflict}
                className={cn(
                  'relative rounded-lg p-2 text-sm transition-all',
                  past && 'cursor-not-allowed opacity-40',
                  conflict && 'cursor-not-allowed bg-error-50 text-error-600',
                  !past && !conflict && 'hover:bg-slate-100',
                  today && !selected && 'border-2 border-primary-300',
                  selected && 'bg-primary-600 text-white font-semibold'
                )}
                whileHover={!past && !conflict ? { scale: 1.05 } : {}}
                whileTap={!past && !conflict ? { scale: 0.95 } : {}}
              >
                {date.getDate()}
                {conflict && (
                  <AlertCircle className="absolute right-1 top-1 h-3 w-3 text-error-600" />
                )}
                {selected && (
                  <CheckCircle2 className="absolute right-1 top-1 h-3 w-3 text-white" />
                )}
              </motion.button>
            );
          })}
        </div>
      </div>

      {/* Time Slots */}
      {selectedDate && (
        <div className="space-y-3">
          <div className="flex items-center gap-2">
            <Clock className="h-5 w-5 text-primary-600" />
            <h3 className="text-lg font-semibold">Saat Seçin</h3>
          </div>

          {slotsLoading ? (
            <div className="grid grid-cols-4 gap-2">
              {[1, 2, 3, 4].map((i) => (
                <Skeleton key={i} className="h-10 w-full" />
              ))}
            </div>
          ) : (
            <div className="grid grid-cols-4 gap-2">
              {(availableSlots.length > 0 ? availableSlots : timeSlots).map((slot) => {
                const slotTime = typeof slot === 'string' ? slot : slot.time;
                const isAvailable = typeof slot === 'object' ? slot.available : true;
                const isSelectedSlot = selectedTime === slotTime;

                return (
                  <motion.button
                    key={slotTime}
                    onClick={() => isAvailable && onTimeSelect(slotTime)}
                    disabled={!isAvailable}
                    className={cn(
                      'rounded-lg border-2 p-2 text-sm font-medium transition-all',
                      !isAvailable && 'cursor-not-allowed border-slate-200 bg-slate-50 text-slate-400',
                      isAvailable && !isSelectedSlot && 'border-slate-300 hover:border-primary-300 hover:bg-primary-50',
                      isSelectedSlot && 'border-primary-600 bg-primary-600 text-white'
                    )}
                    whileHover={isAvailable ? { scale: 1.05 } : {}}
                    whileTap={isAvailable ? { scale: 0.95 } : {}}
                  >
                    {slotTime}
                  </motion.button>
                );
              })}
            </div>
          )}

          {availableSlots.length === 0 && !slotsLoading && (
            <div className="rounded-lg border border-warning-200 bg-warning-50 p-3 text-sm text-warning-800">
              <AlertCircle className="mr-2 inline h-4 w-4" />
              Bu tarih için müsait saat bulunmamaktadır. Lütfen başka bir tarih seçin.
            </div>
          )}
        </div>
      )}
    </div>
  );
};

