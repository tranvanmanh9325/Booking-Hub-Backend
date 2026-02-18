package com.example.booking.service;

import com.example.booking.dto.ShowtimeDTO;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.ConflictException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.model.Content;
import com.example.booking.model.Screen;
import com.example.booking.model.Showtime;
import com.example.booking.repository.CinemaRepository;
import com.example.booking.repository.ContentRepository;
import com.example.booking.repository.ScreenRepository;
import com.example.booking.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final ContentRepository contentRepository;
    private final ScreenRepository screenRepository;
    private final CinemaRepository cinemaRepository;

    public List<ShowtimeDTO> getShowtimesByContent(Long contentId) {
        return showtimeRepository.findByContentId(contentId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShowtimeDTO createShowtime(ShowtimeDTO showtimeDTO) {
        // 1. Validate Content
        Content content = contentRepository.findById(showtimeDTO.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        // 2. Resolve Cinema
        com.example.booking.model.Cinema cinema = null;
        Long cinemaId = showtimeDTO.getCinemaId();

        if (cinemaId != null) {
            cinema = cinemaRepository.findById(cinemaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        } else if (showtimeDTO.getCinemaName() != null && !showtimeDTO.getCinemaName().trim().isEmpty()) {
            String cinemaName = showtimeDTO.getCinemaName().trim();
            cinema = cinemaRepository.findByName(cinemaName)
                    .orElseGet(() -> {
                        com.example.booking.model.Cinema newCinema = new com.example.booking.model.Cinema();
                        newCinema.setName(cinemaName);
                        newCinema.setCity("Hồ Chí Minh");
                        newCinema.setAddress(showtimeDTO.getCinemaAddress() != null
                                && !showtimeDTO.getCinemaAddress().trim().isEmpty()
                                        ? showtimeDTO.getCinemaAddress().trim()
                                        : "Cập nhật địa chỉ");
                        return cinemaRepository.save(newCinema);
                    });
        } else {
            throw new BadRequestException("Cinema ID or Name is required");
        }

        // 3. Resolve Screen
        Screen screen = null;
        Long screenId = showtimeDTO.getScreenId();

        if (screenId != null) {
            screen = screenRepository.findById(screenId)
                    .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));
            if (!screen.getCinema().getId().equals(cinema.getId())) {
                throw new BadRequestException("Screen does not belong to the selected Cinema");
            }
        } else if (showtimeDTO.getScreenName() != null && !showtimeDTO.getScreenName().trim().isEmpty()) {
            String screenName = showtimeDTO.getScreenName().trim();
            final com.example.booking.model.Cinema finalCinema = cinema;
            screen = screenRepository.findByNameAndCinemaId(screenName, cinema.getId())
                    .orElseGet(() -> {
                        Screen newScreen = new Screen();
                        newScreen.setName(screenName);
                        newScreen.setCinema(finalCinema);
                        newScreen.setScreenType("2D");
                        newScreen.setCapacity(100);
                        return screenRepository.save(newScreen);
                    });
        } else {
            throw new BadRequestException("Screen ID or Name is required");
        }

        // 4. Resolve Times
        if (showtimeDTO.getStartTime() == null) {
            throw new BadRequestException("Start time is required");
        }

        LocalDateTime firstStartTime = showtimeDTO.getStartTime();
        int duration = content.getDuration() != null ? content.getDuration() : 120;

        List<Showtime> createdShowtimes = new ArrayList<>();

        // Loop logic
        java.time.LocalDate loopDate = firstStartTime.toLocalDate();
        java.time.LocalDate endDate = showtimeDTO.getRepeatUntilDate() != null
                ? showtimeDTO.getRepeatUntilDate()
                : loopDate;

        // Safety check: max loop to prevent infinite creation by mistake (e.g. 1 year)
        if (showtimeDTO.getRepeatUntilDate() != null
                && showtimeDTO.getRepeatUntilDate().isAfter(loopDate.plusDays(90))) {
            throw new BadRequestException("Cannot schedule recurring showtimes more than 90 days in advance.");
        }

        // We use a counter to iterate days
        int daysToAdd = 0;

        while (!loopDate.plusDays(daysToAdd).isAfter(endDate)) {
            LocalDateTime currentStartTime = firstStartTime.plusDays(daysToAdd);
            LocalDateTime currentEndTime = currentStartTime.plusMinutes(duration);

            // Check conflicts for this slot
            List<Showtime> conflicts = showtimeRepository.findConflictingShowtimes(screen.getId(), currentStartTime,
                    currentEndTime);
            if (!conflicts.isEmpty()) {
                // For recurring, we might want to SKIP conflicting slots or FAIl all.
                // "Senior" approach: fail all guarantees integrity. Skipping might confuse user
                // ("Why is Wednesday missing?").
                throw new ConflictException(
                        "Screen is blocked at " + currentStartTime + ". Recurring operation cancelled.");
            }

            Showtime showtime = new Showtime();
            showtime.setContent(content);
            showtime.setScreen(screen);
            showtime.setStartTime(currentStartTime);
            showtime.setEndTime(currentEndTime);
            showtime.setPrice(showtimeDTO.getPrice() != null ? showtimeDTO.getPrice() : 85000.0);
            showtime.setMovie(null); // Explicitly null per new logic

            createdShowtimes.add(showtimeRepository.save(showtime));

            daysToAdd++;
        }

        return toDTO(createdShowtimes.get(0));
    }

    public ShowtimeDTO updateShowtime(Long id, ShowtimeDTO showtimeDTO) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        if (showtimeDTO.getContentId() == null || !showtime.getContent().getId().equals(showtimeDTO.getContentId())) {
            throw new BadRequestException("Cannot change content of a showtime");
        }

        // Handle Cinema lookup/creation (similar to create)
        com.example.booking.model.Cinema cinema = null;
        Long cinemaId = showtimeDTO.getCinemaId();

        if (cinemaId != null) {
            cinema = cinemaRepository.findById(cinemaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        } else if (showtimeDTO.getCinemaName() != null && !showtimeDTO.getCinemaName().trim().isEmpty()) {
            String cinemaName = showtimeDTO.getCinemaName().trim();
            cinema = cinemaRepository.findByName(cinemaName)
                    .orElseGet(() -> {
                        com.example.booking.model.Cinema newCinema = new com.example.booking.model.Cinema();
                        newCinema.setName(cinemaName);
                        newCinema.setCity("Hồ Chí Minh");
                        newCinema.setAddress(showtimeDTO.getCinemaAddress() != null
                                && !showtimeDTO.getCinemaAddress().trim().isEmpty()
                                        ? showtimeDTO.getCinemaAddress().trim()
                                        : "Cập nhật địa chỉ");
                        return cinemaRepository.save(newCinema);
                    });
        } else {
            // If cinema name/id not provided, keep existing? Or throw?
            // For simplify, let's assume we must provide it or it stays same if we map it?
            // Actually, the DTO comes from form, so it should have data.
            // If not provided in DTO, we should probably keep existing if that's the logic,
            // but here we expect full update payload usually.
            // Let's enforce requirement as in create.
            throw new BadRequestException("Cinema ID or Name is required");
        }

        // Handle Screen lookup/creation
        Screen screen = null;
        Long screenId = showtimeDTO.getScreenId();

        if (screenId != null) {
            screen = screenRepository.findById(screenId)
                    .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));
            if (!screen.getCinema().getId().equals(cinema.getId())) {
                throw new BadRequestException("Screen does not belong to the selected Cinema");
            }
        } else if (showtimeDTO.getScreenName() != null && !showtimeDTO.getScreenName().trim().isEmpty()) {
            String screenName = showtimeDTO.getScreenName().trim();
            final com.example.booking.model.Cinema finalCinema = cinema;
            screen = screenRepository.findByNameAndCinemaId(screenName, cinema.getId())
                    .orElseGet(() -> {
                        Screen newScreen = new Screen();
                        newScreen.setName(screenName);
                        newScreen.setCinema(finalCinema);
                        newScreen.setScreenType("2D");
                        newScreen.setCapacity(100);
                        return screenRepository.save(newScreen);
                    });
        } else {
            throw new BadRequestException("Screen ID or Name is required");
        }

        // Update times
        if (showtimeDTO.getStartTime() == null) {
            throw new BadRequestException("Start time is required");
        }
        LocalDateTime startTime = showtimeDTO.getStartTime();
        int duration = showtime.getContent().getDuration() != null ? showtime.getContent().getDuration() : 120;
        LocalDateTime endTime = startTime.plusMinutes(duration);

        // Check conflicts (excluding current showtime)
        // We need a repo method for this or filter in memory (not efficient) or custom
        // query.
        // Let's use existing findConflictingShowtimes and filter out `id` or create new
        // query.
        // For now, let's assume distinct check is needed.
        // V1 implementation: check simple conflict
        List<Showtime> conflicts = showtimeRepository.findConflictingShowtimes(screen.getId(), startTime, endTime);
        boolean hasConflict = conflicts.stream().anyMatch(s -> !s.getId().equals(id));
        if (hasConflict) {
            throw new ConflictException("Screen is already booked for this time slot");
        }

        showtime.setScreen(screen);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        showtime.setPrice(showtimeDTO.getPrice());

        // Movie is null as per new logic
        showtime.setMovie(null);

        Showtime saved = showtimeRepository.save(showtime);

        // Handle Repetition for Update (Extend schedule)
        if (showtimeDTO.getRepeatUntilDate() != null) {
            java.time.LocalDate loopDate = startTime.toLocalDate().plusDays(1);
            java.time.LocalDate endDate = showtimeDTO.getRepeatUntilDate();

            // Safety check
            if (endDate.isAfter(startTime.toLocalDate().plusDays(90))) {
                throw new BadRequestException("Cannot schedule recurring showtimes more than 90 days in advance.");
            }

            int daysToAdd = 1;
            int loopDuration = showtime.getContent().getDuration() != null ? showtime.getContent().getDuration() : 120;

            while (!loopDate.isAfter(endDate)) {
                LocalDateTime currentStartTime = startTime.plusDays(daysToAdd);
                LocalDateTime currentEndTime = currentStartTime.plusMinutes(loopDuration);

                // Check conflicts
                List<Showtime> loopConflicts = showtimeRepository.findConflictingShowtimes(screen.getId(),
                        currentStartTime,
                        currentEndTime);
                if (loopConflicts.isEmpty()) {
                    Showtime newShowtime = new Showtime();
                    newShowtime.setContent(showtime.getContent());
                    newShowtime.setScreen(screen);
                    newShowtime.setStartTime(currentStartTime);
                    newShowtime.setEndTime(currentEndTime);
                    newShowtime.setPrice(saved.getPrice());
                    newShowtime.setMovie(null);
                    showtimeRepository.save(newShowtime);
                }
                // If conflict, we skip silently or throw?
                // For update extension, skipping silent might be better or throwing?
                // Adhering to "Senior" strictness from create: Fail if blocked.
                else {
                    throw new ConflictException(
                            "Screen is blocked at " + currentStartTime + ". Recurring extension cancelled.");
                }

                loopDate = loopDate.plusDays(1);
                daysToAdd++;
            }
        }

        return toDTO(saved);
    }

    public void deleteShowtime(Long id) {
        if (id == null) {
            throw new BadRequestException("ID is required");
        }
        if (!showtimeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Showtime not found");
        }
        showtimeRepository.deleteById(id);
    }

    private ShowtimeDTO toDTO(Showtime showtime) {
        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setId(showtime.getId());
        dto.setStartTime(showtime.getStartTime());
        dto.setEndTime(showtime.getEndTime());
        dto.setPrice(showtime.getPrice());

        if (showtime.getContent() != null) {
            dto.setContentId(showtime.getContent().getId());
            dto.setContentName(showtime.getContent().getName());
        }

        if (showtime.getScreen() != null) {
            dto.setScreenId(showtime.getScreen().getId());
            dto.setScreenName(showtime.getScreen().getName());
            if (showtime.getScreen().getCinema() != null) {
                dto.setCinemaId(showtime.getScreen().getCinema().getId());
                dto.setCinemaName(showtime.getScreen().getCinema().getName());
                dto.setCinemaAddress(showtime.getScreen().getCinema().getAddress());
            }
        }

        return dto;
    }
}