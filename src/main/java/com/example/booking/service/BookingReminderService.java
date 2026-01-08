package com.example.booking.service;

import com.example.booking.model.HotelBooking;
import com.example.booking.model.MovieBooking;
import com.example.booking.repository.HotelBookingRepository;
import com.example.booking.repository.MovieBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingReminderService {

    private static final Logger logger = LoggerFactory.getLogger(BookingReminderService.class);

    private final HotelBookingRepository hotelBookingRepository;
    private final MovieBookingRepository movieBookingRepository;
    private final EmailService emailService;

    public BookingReminderService(HotelBookingRepository hotelBookingRepository,
            MovieBookingRepository movieBookingRepository,
            EmailService emailService) {
        this.hotelBookingRepository = hotelBookingRepository;
        this.movieBookingRepository = movieBookingRepository;
        this.emailService = emailService;
    }

    /**
     * Run every day at 9:00 AM to send reminders for check-ins happening tomorrow.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(readOnly = true)
    public void sendCheckInReminders() {
        logger.info("Starting check-in reminder job...");
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // 1. Hotel Reminders
        List<HotelBooking> hotelBookings = hotelBookingRepository.findByCheckInAndStatus(tomorrow, "CONFIRMED");
        logger.info("Found {} hotel bookings for tomorrow ({})", hotelBookings.size(), tomorrow);

        for (HotelBooking booking : hotelBookings) {
            try {
                emailService.sendCheckInReminder(booking.getUser().getEmail(), booking.getUser().getFullName(),
                        booking);
            } catch (Exception e) {
                logger.error("Failed to send hotel reminder for booking {}", booking.getId(), e);
            }
        }

        // 2. Movie Reminders (Showtimes between tomorrow 00:00 and 23:59)
        LocalDateTime startOfDay = tomorrow.atStartOfDay();
        LocalDateTime endOfDay = tomorrow.atTime(LocalTime.MAX);

        List<MovieBooking> movieBookings = movieBookingRepository.findByShowtimeStartTimeBetweenAndStatus(startOfDay,
                endOfDay, "CONFIRMED");
        logger.info("Found {} movie bookings for tomorrow ({})", movieBookings.size(), tomorrow);

        for (MovieBooking booking : movieBookings) {
            try {
                emailService.sendCheckInReminder(booking.getUser().getEmail(), booking.getUser().getFullName(),
                        booking);
            } catch (Exception e) {
                logger.error("Failed to send movie reminder for booking {}", booking.getId(), e);
            }
        }

        logger.info("Completed check-in reminder job.");
    }
}
