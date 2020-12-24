package com.askwinston.service;

import com.askwinston.model.DoctorSlot;
import com.askwinston.model.Product;
import com.askwinston.model.TimeInterval;
import com.askwinston.model.User;
import com.askwinston.subscription.ProductSubscription;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

    DoctorSlot getById(long doctorSlotId);

    List<DoctorSlot> getAllDoctorSlots(User doctor, List<LocalDate> dates);

    List<DoctorSlot> getAvailableDoctorSlots(User doctor, List<LocalDate> dates);

    List<DoctorSlot> getBookedDoctorSlots(User doctor, List<LocalDate> dates);

    List<DoctorSlot> getAvailableSlotsByProblemCategory(Product.ProblemCategory category, List<LocalDate> dates);

    List<DoctorSlot> getPatientBookings(User patient);

    DoctorSlot bookAnAppointment(long doctorSlotId, User patient);

    DoctorSlot bookAnAppointment(long doctorSlotId, User patient, List<ProductSubscription> subscriptions);

    DoctorSlot cancelAnAppointment(long doctorSlotId, User user);

    List<DoctorSlot> createSchedule(User doctor, List<TimeInterval> intervals, boolean isRecurring);

    void deleteSlot(User user, List<Long> slotIdList);
}
