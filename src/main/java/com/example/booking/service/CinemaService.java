package com.example.booking.service;

import com.example.booking.dto.CinemaDTO;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.mapper.MovieMapper;
import com.example.booking.model.Cinema;
import com.example.booking.repository.CinemaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final MovieMapper movieMapper;

    public CinemaService(CinemaRepository cinemaRepository, MovieMapper movieMapper) {
        this.cinemaRepository = cinemaRepository;
        this.movieMapper = movieMapper;
    }

    @Transactional(readOnly = true)
    public List<CinemaDTO> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(movieMapper::toCinemaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CinemaDTO getCinemaById(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found with id: " + id));
        return movieMapper.toCinemaDTO(cinema);
    }

    public CinemaDTO createCinema(CinemaDTO cinemaDTO) {
        Cinema cinema = movieMapper.toCinema(cinemaDTO);
        Cinema savedCinema = cinemaRepository.save(cinema);
        return movieMapper.toCinemaDTO(savedCinema);
    }

    public CinemaDTO updateCinema(Long id, CinemaDTO cinemaDTO) {
        Cinema existingCinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found with id: " + id));

        existingCinema.setName(cinemaDTO.getName());
        existingCinema.setAddress(cinemaDTO.getAddress());
        existingCinema.setCity(cinemaDTO.getCity());
        existingCinema.setFacilities(cinemaDTO.getFacilities());
        existingCinema.setPhoneNumber(cinemaDTO.getPhoneNumber());

        Cinema updatedCinema = cinemaRepository.save(existingCinema);
        return movieMapper.toCinemaDTO(updatedCinema);
    }

    public void deleteCinema(Long id) {
        if (!cinemaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cinema not found with id: " + id);
        }
        cinemaRepository.deleteById(id);
    }
}
