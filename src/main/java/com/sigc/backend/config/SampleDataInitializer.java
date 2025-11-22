package com.sigc.backend.config;

import com.sigc.backend.model.*;
import com.sigc.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Inicializador de datos de ejemplo
 * Crea especialidades, doctores y horarios de prueba
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2) // Se ejecuta después del DataInitializer
public class SampleDataInitializer implements CommandLineRunner {

    private final EspecialidadRepository especialidadRepository;
    private final DoctorRepository doctorRepository;
    private final HorarioRepository horarioRepository;

    @Override
    public void run(String... args) throws Exception {
        if (especialidadRepository.count() == 0) {
            log.info("🔄 Creando datos de ejemplo...");
            crearEspecialidades();
            crearDoctores();
            crearHorarios();
            log.info("✅ Datos de ejemplo creados exitosamente");
        } else {
            log.info("✅ Ya existen datos en la base de datos");
        }
    }

    private void crearEspecialidades() {
        // Especialidad 1: Medicina General
        Especialidad medicinaGeneral = new Especialidad();
        medicinaGeneral.setNombre("Medicina General");
        medicinaGeneral.setDescripcion("Atención médica integral para todas las edades");
        especialidadRepository.save(medicinaGeneral);

        // Especialidad 2: Pediatría
        Especialidad pediatria = new Especialidad();
        pediatria.setNombre("Pediatría");
        pediatria.setDescripcion("Especialidad médica dedicada al cuidado de niños y adolescentes");
        especialidadRepository.save(pediatria);

        // Especialidad 3: Cardiología
        Especialidad cardiologia = new Especialidad();
        cardiologia.setNombre("Cardiología");
        cardiologia.setDescripcion("Especialidad médica del corazón y sistema circulatorio");
        especialidadRepository.save(cardiologia);

        // Especialidad 4: Dermatología
        Especialidad dermatologia = new Especialidad();
        dermatologia.setNombre("Dermatología");
        dermatologia.setDescripcion("Especialidad médica de la piel y sus enfermedades");
        especialidadRepository.save(dermatologia);

        log.info("✅ 4 especialidades creadas");
    }

    private void crearDoctores() {
        Especialidad medicinaGeneral = especialidadRepository.findById(1L).orElse(null);
        Especialidad pediatria = especialidadRepository.findById(2L).orElse(null);
        Especialidad cardiologia = especialidadRepository.findById(3L).orElse(null);

        // Doctor 1
        Doctor doctor1 = new Doctor();
        doctor1.setNombre("Juan Carlos");
        doctor1.setApellido("Pérez García");
        doctor1.setCorreo("juan.perez@hospital.com");
        doctor1.setTelefono("987654321");
        doctor1.setEspecialidad(medicinaGeneral);
        doctorRepository.save(doctor1);

        // Doctor 2
        Doctor doctor2 = new Doctor();
        doctor2.setNombre("María Elena");
        doctor2.setApellido("Rodríguez López");
        doctor2.setCorreo("maria.rodriguez@hospital.com");
        doctor2.setTelefono("987654322");
        doctor2.setEspecialidad(pediatria);
        doctorRepository.save(doctor2);

        // Doctor 3
        Doctor doctor3 = new Doctor();
        doctor3.setNombre("Roberto");
        doctor3.setApellido("Fernández Silva");
        doctor3.setCorreo("roberto.fernandez@hospital.com");
        doctor3.setTelefono("987654323");
        doctor3.setEspecialidad(cardiologia);
        doctorRepository.save(doctor3);

        log.info("✅ 3 doctores creados");
    }

    private void crearHorarios() {
        Doctor doctor1 = doctorRepository.findById(1L).orElse(null);
        Doctor doctor2 = doctorRepository.findById(2L).orElse(null);
        Doctor doctor3 = doctorRepository.findById(3L).orElse(null);

        LocalDate hoy = LocalDate.now();

        // Horarios para próximos 5 días
        for (int i = 0; i < 5; i++) {
            LocalDate fecha = hoy.plusDays(i);

            // Horario mañana - Doctor 1
            Horario horario1 = new Horario();
            horario1.setDoctor(doctor1);
            horario1.setFecha(fecha);
            horario1.setHoraInicio(LocalTime.of(8, 0));
            horario1.setHoraFin(LocalTime.of(12, 0));
            horario1.setTurno("Mañana");
            horario1.setDisponible(true);
            horarioRepository.save(horario1);

            // Horario tarde - Doctor 1
            Horario horario2 = new Horario();
            horario2.setDoctor(doctor1);
            horario2.setFecha(fecha);
            horario2.setHoraInicio(LocalTime.of(14, 0));
            horario2.setHoraFin(LocalTime.of(18, 0));
            horario2.setTurno("Tarde");
            horario2.setDisponible(true);
            horarioRepository.save(horario2);

            // Horario mañana - Doctor 2
            Horario horario3 = new Horario();
            horario3.setDoctor(doctor2);
            horario3.setFecha(fecha);
            horario3.setHoraInicio(LocalTime.of(9, 0));
            horario3.setHoraFin(LocalTime.of(13, 0));
            horario3.setTurno("Mañana");
            horario3.setDisponible(true);
            horarioRepository.save(horario3);

            // Horario tarde - Doctor 3
            Horario horario4 = new Horario();
            horario4.setDoctor(doctor3);
            horario4.setFecha(fecha);
            horario4.setHoraInicio(LocalTime.of(15, 0));
            horario4.setHoraFin(LocalTime.of(19, 0));
            horario4.setTurno("Tarde");
            horario4.setDisponible(true);
            horarioRepository.save(horario4);
        }

        log.info("✅ 20 horarios creados (5 días x 4 horarios)");
    }
}
