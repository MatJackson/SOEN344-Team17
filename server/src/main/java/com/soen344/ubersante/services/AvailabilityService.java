package com.soen344.ubersante.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.soen344.ubersante.dto.AvailabilityDetails;
import com.soen344.ubersante.exceptions.DateNotFoundException;
import com.soen344.ubersante.exceptions.DoctorNotFoundException;
import com.soen344.ubersante.exceptions.InvalidAppointmentException;
import com.soen344.ubersante.exceptions.PatientNotFoundException;
import com.soen344.ubersante.exceptions.EmptyCartException;
import com.soen344.ubersante.models.Appointment;
import com.soen344.ubersante.models.Availability;
import com.soen344.ubersante.models.Patient;
import com.soen344.ubersante.repositories.AppointmentRepository;
import com.soen344.ubersante.repositories.AvailabilityRepository;
import com.soen344.ubersante.repositories.DoctorRepository;
import com.soen344.ubersante.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AvailabilityService {

    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    public List<Availability> getAvailabilityByMonth(String month, String availabilityType) throws DateNotFoundException, InvalidAppointmentException, NumberFormatException {

        int monthVal = Integer.parseInt(month);
        String availType;
        if (monthVal <= 0 || monthVal > 12) {
            throw new DateNotFoundException("Invalid month value.");
        }

        if (availabilityType.equalsIgnoreCase("walkin")) {
            availType = "0";
        } else if (availabilityType.equalsIgnoreCase("annual")) {
            availType = "1";
        } else {
            throw new InvalidAppointmentException("Invalid appointment type.");
        }

        return availabilityRepository.findAvailabilitiesByMonth(month, availType);
    }

    public void addAppointmentToTable(AvailabilityDetails availability, Appointment appointment) {
        availabilityRepository.addAppointmentToAvailability(availability.getId() ,appointment.getId());
    }

    public boolean availabilityToAppointment(Patient patient, List<AvailabilityDetails> availabilityDetailsCart) throws PatientNotFoundException, EmptyCartException, DoctorNotFoundException {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());

        if (patientRepository.findByHealthCard(patient.getHealthCard()) == null) {
            throw new PatientNotFoundException("Patient not found in " + this);
        }

        if (availabilityDetailsCart.size() <= 0) {
            throw new EmptyCartException("Nothing in the cart");
        }

        for (AvailabilityDetails details : availabilityDetailsCart) {

            if (doctorRepository.findByPermitNumber(details.getDoctorPermitNumber()) == null) {
                throw new DoctorNotFoundException("Doctor not found in " + this);
            }

            Appointment appointment = new Appointment(
                    patientRepository.findByHealthCard(patient.getHealthCard()),
                    doctorRepository.findByPermitNumber(details.getDoctorPermitNumber()),
                    patient.getFirstName() + " " + patient.getLastName(),
                    details.getAppointmentType(),
                    LocalDateTime.parse(details.getStartTime()),
                    LocalDateTime.parse(details.getEndTime()),
                    ts);
            appointmentRepository.save(appointment);
            addAppointmentToTable(details, appointment);
        }
        return true;
    }
}