package com.example.routeservice.exception;

import com.example.routeservice.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        ErrorResponse errorResponse;

        if (exception instanceof RouteNotFoundException) {
            errorResponse = new ErrorResponse(
                    404,
                    "Маршрут не найден",
                    null,
                    List.of(exception.getMessage())
            );
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .build();
        } else if (exception instanceof ValidationException) {
            ValidationException ve = (ValidationException) exception;
            errorResponse = new ErrorResponse(
                    400,
                    "Ошибка валидации данных",
                    null,
                    ve.getErrors()
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            List<String> errors = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());

            errorResponse = new ErrorResponse(
                    400,
                    "Ошибка валидации данных",
                    null,
                    errors
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        } else {
            errorResponse = new ErrorResponse(
                    500,
                    "Внутренняя ошибка сервера",
                    null,
                    List.of(exception.getMessage())
            );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .build();
        }
    }
}