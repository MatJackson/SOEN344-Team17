package com.soen344.ubersante.services;

import com.soen344.ubersante.dto.AppointmentDetails;
import com.soen344.ubersante.dto.PatientDetails;
import com.soen344.ubersante.exceptions.NoAppointmentException;
import com.soen344.ubersante.models.Appointment;
import com.soen344.ubersante.models.Doctor;
import com.soen344.ubersante.models.Patient;
import com.soen344.ubersante.repositories.AppointmentRepository;
import com.soen344.ubersante.repositories.AvailabilityRepository;
import com.soen344.ubersante.repositories.DoctorRepository;
import com.soen344.ubersante.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Appointment> findAppointmentForPatient(PatientDetails patientDetails) throws NoAppointmentException {
        Patient patient = patientRepository.findByHealthCard(patientDetails.getHealthCard());
        List<Appointment> listAppointment = appointmentRepository.findAppointmentByPatientId(patient.getId());

        if (listAppointment == null) {
            throw new NoAppointmentException("No appointment found for patient id " + patient.getId());
        }

        return listAppointment;
    }

    public List<AppointmentDetails> getAppointmentDetails(List<Appointment> appointmentList) {
        List<AppointmentDetails> detailList = new ArrayList<>();
        for (Appointment appointment: appointmentList) {
            AppointmentDetails appointmentDetails = new AppointmentDetails (
                appointment.getId(),
                appointment.getPatient(),
                appointment.getDoctor(),
                appointment.getCreatedBy(),
                appointment.getAppointmentType(),
                appointment.getDate().toLocalDate().toString(),
                appointment.getTime().toLocalTime().toString(),
                appointment.getCreatedAt().toString(),
                appointment.getClinic().getName()
            );
            detailList.add(appointmentDetails);
        }
        return detailList;
    }

    public List<AppointmentDetails> getAppointmentDetailsForDoctor(String permit) {
        Doctor doctor = doctorRepository.findByPermitNumber(permit);
        return getAppointmentDetails(appointmentRepository.findAllByDoctor(doctor));
    }

    public void cancelAppointmentforPatient(long id) {
        appointmentRepository.deleteAppointmentById(id);
        availabilityRepository.updateAvailabilitiesByAppointmentId(id);
    }
}
