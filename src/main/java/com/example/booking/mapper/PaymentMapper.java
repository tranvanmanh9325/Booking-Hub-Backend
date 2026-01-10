package com.example.booking.mapper;

import com.example.booking.dto.PaymentDTO;
import com.example.booking.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
// Trigger IDE re-analysis
public interface PaymentMapper {
    PaymentDTO toPaymentDTO(Payment payment);
}
