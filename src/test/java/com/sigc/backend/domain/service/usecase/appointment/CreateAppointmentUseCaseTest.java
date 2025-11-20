package com.sigc.backend.domain.service.usecase.appointment;

import com.sigc.backend.domain.exception.AppointmentInvalidException;
import com.sigc.backend.domain.model.Cita;
import com.sigc.backend.domain.port.ICitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateAppointmentUseCaseTest {

    private ICitaRepository citaRepository;
    private CreateAppointmentUseCase useCase;

    @BeforeEach
    void setup() {
        citaRepository = Mockito.mock(ICitaRepository.class);
        useCase = new CreateAppointmentUseCase(citaRepository);
    }

    @Test
    void execute_withPastDate_throwsAppointmentInvalidException() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                LocalDateTime.now().minusDays(1),
                "Consulta",
                1L,
                1L
        );

        assertThrows(AppointmentInvalidException.class, () -> useCase.execute(request));
        verify(citaRepository, never()).save(any());
    }

    @Test
    void execute_withValidData_savesAndReturnsResponse() {
        LocalDateTime date = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0);
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                date,
                "Consulta general",
                1L,
                1L
        );

        // Mock save to return a Cita with ID assigned
        when(citaRepository.save(any())).thenAnswer(invocation -> {
            Cita c = (Cita) invocation.getArgument(0);
            c.setId(123L);
            return c;
        });

        CreateAppointmentResponse response = useCase.execute(request);

        assertNotNull(response);
        assertEquals(123L, response.getAppointmentId());
        verify(citaRepository, times(1)).save(any());
    }
}
