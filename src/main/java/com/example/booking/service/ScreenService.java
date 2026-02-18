package com.example.booking.service;

import com.example.booking.dto.ScreenDTO;
import com.example.booking.dto.SeatDTO;
import com.example.booking.dto.request.SeatGenerationRequest;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.mapper.MovieMapper;
import com.example.booking.model.Cinema;
import com.example.booking.model.Screen;
import com.example.booking.model.Seat;
import com.example.booking.repository.CinemaRepository;
import com.example.booking.repository.ScreenRepository;
import com.example.booking.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;
    private final MovieMapper movieMapper;

    public ScreenService(ScreenRepository screenRepository, CinemaRepository cinemaRepository,
            SeatRepository seatRepository, MovieMapper movieMapper) {
        this.screenRepository = screenRepository;
        this.cinemaRepository = cinemaRepository;
        this.seatRepository = seatRepository;
        this.movieMapper = movieMapper;
    }

    @Transactional(readOnly = true)
    public List<ScreenDTO> getScreensByCinema(Long cinemaId) {
        return screenRepository.findByCinemaId(cinemaId).stream()
                .map(movieMapper::toScreenDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScreenDTO getScreenById(Long id) {
        Screen screen = screenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + id));
        return movieMapper.toScreenDTO(screen);
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsByScreenId(Long screenId) {
        return seatRepository.findByScreenId(screenId).stream()
                // false for isBooked as this is just structure view
                .map(seat -> movieMapper.toSeatDTO(seat, false))
                .collect(Collectors.toList());
    }

    public ScreenDTO createScreen(ScreenDTO screenDTO) {
        Cinema cinema = cinemaRepository.findById(screenDTO.getCinemaId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cinema not found with id: " + screenDTO.getCinemaId()));

        Screen screen = movieMapper.toScreen(screenDTO);
        screen.setCinema(cinema);
        screen.setCapacity(0); // Initial capacity usually derived from seats, or set manually.

        Screen savedScreen = screenRepository.save(screen);
        return movieMapper.toScreenDTO(savedScreen);
    }

    public ScreenDTO updateScreen(Long id, ScreenDTO screenDTO) {
        Screen existingScreen = screenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + id));

        existingScreen.setName(screenDTO.getName());
        // Capacity might be updated by seat generation, but allow manual override if
        // needed or ignore
        // existingScreen.setCapacity(screenDTO.getCapacity());
        existingScreen.setScreenType(screenDTO.getScreenType());

        if (screenDTO.getCinemaId() != null && !screenDTO.getCinemaId().equals(existingScreen.getCinema().getId())) {
            Cinema newCinema = cinemaRepository.findById(screenDTO.getCinemaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cinema not found with id: " + screenDTO.getCinemaId()));
            existingScreen.setCinema(newCinema);
        }

        Screen updatedScreen = screenRepository.save(existingScreen);
        return movieMapper.toScreenDTO(updatedScreen);
    }

    public void deleteScreen(Long id) {
        if (!screenRepository.existsById(id)) {
            throw new ResourceNotFoundException("Screen not found with id: " + id);
        }
        // Should delete seats first? JPA might handle cascade if configured, otherwise
        // manual delete
        // List<Seat> seats = seatRepository.findByScreenId(id);
        // seatRepository.deleteAll(seats);
        screenRepository.deleteById(id);
    }

    public void generateSeats(Long screenId, SeatGenerationRequest request) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));

        // Delete existing seats
        List<Seat> existingSeats = seatRepository.findByScreenId(screenId);
        seatRepository.deleteAll(existingSeats);

        List<Seat> newSeats = new ArrayList<>();
        // SeatGenerationRequest has seats list, rows, cols.
        // Assuming request.seats contains definitions if provided, or we generate grid.
        // If request.seats is null/empty, we generate grid based on rows/cols?
        // Let's assume simple grid generation for now based on rows/cols if seats list
        // is empty.

        if (request.getSeats() != null && !request.getSeats().isEmpty()) {
            // Implement based on provided definitions if needed
        } else {
            int rows = request.getRowCount(); // Was getRows() in my previous attempt, but logic says RowCount based on
                                              // DTO
            int cols = request.getColCount();

            for (int i = 0; i < rows; i++) {
                char rowChar = (char) ('A' + i);
                for (int j = 1; j <= cols; j++) {
                    Seat seat = new Seat();
                    seat.setScreen(screen);
                    seat.setRow(String.valueOf(rowChar));
                    seat.setNumber(j);
                    seat.setSeatType("Standard");
                    newSeats.add(seat);
                }
            }
        }

        seatRepository.saveAll(newSeats);

        // Update capacity
        screen.setCapacity(newSeats.size());
        screenRepository.save(screen);
    }
}