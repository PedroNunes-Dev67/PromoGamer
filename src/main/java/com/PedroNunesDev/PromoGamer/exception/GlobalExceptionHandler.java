package com.PedroNunesDev.PromoGamer.exception;

import feign.FeignException;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(FeignException ex, HttpServletRequest request) {
        log.warn("Recurso não encontrado na CheapShark API: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Deals não encontrados para os parâmetros informados.",
                request
        );
    }

    @ExceptionHandler(FeignException.TooManyRequests.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(FeignException ex, HttpServletRequest request) {
        log.warn("Rate limit atingido na CheapShark API: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                "Limite de requisições excedido, tente novamente mais tarde.",
                request
        );
    }


    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handleTimeout(RetryableException ex, HttpServletRequest request) {
        log.error("Timeout ao chamar CheapShark API", ex);
        return buildResponse(
                HttpStatus.GATEWAY_TIMEOUT,
                "Serviço externo demorou para responder.",
                request
        );
    }

    // Captura qualquer outro erro HTTP do Feign não tratado especificamente acima
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex, HttpServletRequest request) {
        log.error("Erro na chamada à CheapShark API - status: {}, body: {}",
                ex.status(), ex.contentUTF8(), ex);
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "Erro ao consultar o serviço de deals.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor.",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}