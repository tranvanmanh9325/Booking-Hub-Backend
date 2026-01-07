package com.example.booking.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class XssSanitizerService {

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return Jsoup.clean(input, Safelist.basic());
    }
}
