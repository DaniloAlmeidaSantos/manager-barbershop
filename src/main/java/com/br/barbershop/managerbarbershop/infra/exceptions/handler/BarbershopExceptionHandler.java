package com.br.barbershop.managerbarbershop.infra.exceptions.handler;

import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import com.br.barbershop.managerbarbershop.infra.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BarbershopExceptionHandler {

    @ExceptionHandler({AuthenticateException.class})
    public ResponseEntity<ApiResponseDTO> handleUnauthorizedException(AuthenticateException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiResponseDTO(
                                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                                "Falha ao autenticar, e-mail ou senha incorretos."
                        )
                );
    }

    @ExceptionHandler({EmailValidatorException.class})
    public ResponseEntity<ApiResponseDTO> handleEmailValidatorException(EmailValidatorException ex) {
        return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .body(
                        new ApiResponseDTO(
                                String.valueOf(HttpStatus.PRECONDITION_FAILED.value()),
                                "E-mail inválido: " + ex.getEmail()
                        )
                );
    }

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<ApiResponseDTO> handleMismatchServicesSchedule(ServiceException ex) {
        return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .body(
                        new ApiResponseDTO(
                                String.valueOf(HttpStatus.PRECONDITION_FAILED.value()),
                                "Existem serviços não existentes sendo informado: " + ex.getMessage()
                        )
                );
    }

    @ExceptionHandler({ScheduleConflictsException.class})
    public ResponseEntity<ApiResponseDTO> handleConflictsOnScheduleDateAndTime(ScheduleConflictsException ex) {
        String scheduleDescriptionException = ex.isMismatchDateTime() ?
                "A data e hora de finalização não pode ser maior que a data e hora de inicio." :
                "Horário indisponível para agendamento.";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO(String.valueOf(HttpStatus.BAD_REQUEST.value()), scheduleDescriptionException));
    }

    @ExceptionHandler({ScheduleNotFoundException.class})
    public ResponseEntity<ApiResponseDTO> handleScheduleNotFound(ScheduleNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage()));
    }

    @ExceptionHandler({ScheduleServicesException.class})
    public ResponseEntity<ApiResponseDTO> handleScheduleServicesError(ScheduleServicesException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO(
                        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "Erro interno ao processar agendamento. Tente novamente."
                ));
    }

    @ExceptionHandler({BarberNotFoundException.class})
    public ResponseEntity<ApiResponseDTO> handleBarberNotFound(BarberNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO(String.valueOf(HttpStatus.NOT_FOUND.value()), ex.getMessage()));
    }

    @ExceptionHandler({UnauthorizedBarberOperationException.class})
    public ResponseEntity<ApiResponseDTO> handleUnauthorizedBarberOperation(UnauthorizedBarberOperationException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponseDTO(String.valueOf(HttpStatus.FORBIDDEN.value()), ex.getMessage()));
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class})
    public ResponseEntity<ApiResponseDTO> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponseDTO(String.valueOf(HttpStatus.CONFLICT.value()), ex.getMessage()));
    }
}
