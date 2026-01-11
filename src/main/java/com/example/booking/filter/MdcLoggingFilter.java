package com.example.booking.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.UUID;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MdcLoggingFilter.class);
    private static final String TRACE_ID_LOG_VAR_NAME = "traceId";
    private static final String TRACE_ID_HEADER_NAME = "X-Trace-Id";
    private static final int MAX_CONTENT_CACHE_SIZE = 1024 * 50; // 50KB

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = request.getHeader(TRACE_ID_HEADER_NAME);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(TRACE_ID_LOG_VAR_NAME, traceId);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_CONTENT_CACHE_SIZE);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        try {
            // Log Request Basic Info
            log.info("Request: method={} uri={}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(wrappedRequest, wrappedResponse);

            long duration = System.currentTimeMillis() - startTime;

            // Log Request Body (if applicable) and Response
            logRequestBody(wrappedRequest);
            logResponse(wrappedResponse, duration);

            wrappedResponse.copyBodyToResponse();

        } finally {
            MDC.remove(TRACE_ID_LOG_VAR_NAME);
        }
    }

    private void logRequestBody(ContentCachingRequestWrapper request) {
        String content = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (content.length() > 0) {
            String maskedContent = maskSensitiveData(content);
            log.info("Request Body: {}", maskedContent);
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        log.info("Response: status={} duration={}ms", response.getStatus(), duration);
        String content = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (content.length() > 0 && isJson(response.getContentType())) {
            // Optionally log response body only for error or specifically needed cases to
            // avoid noise
            // For now, logging it as per requirement
            log.info("Response Body: {}", content);
        }
    }

    private boolean isJson(String contentType) {
        return contentType != null && contentType.contains("application/json");
    }

    private String maskSensitiveData(String content) {
        // Simple regex-based masking for demo purposes.
        // For production, consider using a proper JSON parser or library if structure
        // is complex.
        return content.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                .replaceAll("\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"***\"")
                .replaceAll("\"confirmPassword\"\\s*:\\s*\"[^\"]*\"", "\"confirmPassword\":\"***\"");
    }
}
