package com.networkautomation.networkapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidationError(
                MethodArgumentNotValidException exception) {

                ApiError error = new ApiError(
                        "VALIDATION_ERROR",
                        "Request contains invalid or missing fields"
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(error);
        }

        @ExceptionHandler(DeviceNotFoundException.class)
        public ResponseEntity<ApiError> handleDeviceNotFound(DeviceNotFoundException exception) {

                ApiError error = new ApiError(
                        "DEVICE_NOT_FOUND",
                        exception.getMessage()
                );

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(error);
        }

        @ExceptionHandler(DuplicateDeviceException.class)
        public ResponseEntity<ApiError> handleDuplicateDevice(DuplicateDeviceException exception) {

                ApiError error = new ApiError(
                        "DUPLICATE_DEVICE",
                        exception.getMessage()
                );

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(error);
        }
}