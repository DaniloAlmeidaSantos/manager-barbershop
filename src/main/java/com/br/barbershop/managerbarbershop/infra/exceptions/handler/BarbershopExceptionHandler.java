package com.br.barbershop.managerbarbershop.infra.exceptions.handler;

import com.br.barbershop.managerbarbershop.domain.ApiResponseDTO;
import com.br.barbershop.managerbarbershop.infra.exceptions.AuthenticateException;
import com.br.barbershop.managerbarbershop.infra.exceptions.EmailValidatorException;
import com.br.barbershop.managerbarbershop.infra.exceptions.ScheduleConflictsException;
import com.br.barbershop.managerbarbershop.infra.exceptions.ServiceException;
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
    public  ResponseEntity<ApiResponseDTO> handleEmailValidatorException(EmailValidatorException ex) {
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
}
