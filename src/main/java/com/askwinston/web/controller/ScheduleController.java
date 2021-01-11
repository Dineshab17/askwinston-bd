package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.DoctorSlot;
import com.askwinston.model.Product;
import com.askwinston.model.TimeInterval;
import com.askwinston.model.User;
import com.askwinston.service.ScheduleService;
import com.askwinston.service.UserService;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.SubscriptionEngine;
import com.askwinston.web.dto.*;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
@Slf4j
public class ScheduleController {

    private ScheduleService scheduleService;
    private UserService userService;
    private ParsingHelper parsingHelper;
    private SubscriptionEngine subscriptionEngine;

    public ScheduleController(ScheduleService scheduleService, UserService userService, ParsingHelper parsingHelper, SubscriptionEngine subscriptionEngine) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.parsingHelper = parsingHelper;
        this.subscriptionEngine = subscriptionEngine;
    }

    /**
     * @param problemCategory
     * @param dates
     * @return List<ScheduleDto>
     * To get available slots of the doctor based on specialization or problem category
     */
    @GetMapping(value = "/available")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public List<ScheduleDto> getAvailableSlotsByProblemCategory(@RequestParam Product.ProblemCategory problemCategory, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") List<LocalDate> dates) {
        List<DoctorSlot> slots = scheduleService.getAvailableSlotsByProblemCategory(problemCategory, dates);
        return mapDoctorSlotListToScheduleDtoList(slots, dates);
    }

    /**
     * @param principal
     * @param dates
     * @return List<ScheduleDto>
     * To get all time slots of doctor
     */
    @GetMapping(value = "/doctor/all")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<ScheduleDto> getAllDoctorSlots(@AuthenticationPrincipal AwUserPrincipal principal, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") List<LocalDate> dates) {
        User doctor = userService.getById(principal.getId());
        log.info("Getting all slots of doctor with id {{}", doctor.getId());
        List<DoctorSlot> slots = scheduleService.getAllDoctorSlots(doctor, dates);
        return mapDoctorSlotListToScheduleDtoList(slots, dates);
    }

    /**
     * @param principal
     * @param dates
     * @return List<ScheduleDto>
     * To get free slots of doctor
     */
    @GetMapping(value = "/doctor/free")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<ScheduleDto> getDoctorFreeSlots(@AuthenticationPrincipal AwUserPrincipal principal, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") List<LocalDate> dates) {
        User doctor = userService.getById(principal.getId());
        log.info("Getting free slots of doctor with id {}", doctor.getId());
        List<DoctorSlot> slots = scheduleService.getAvailableDoctorSlots(doctor, dates);
        return mapDoctorSlotListToScheduleDtoList(slots, dates);
    }

    /**
     * @param principal
     * @param dates
     * @return List<ScheduleDto>
     * To get busy slots of doctor
     */
    @GetMapping(value = "/doctor/busy")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'ADMIN')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<ScheduleDto> getDoctorBusySlots(@AuthenticationPrincipal AwUserPrincipal principal, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") List<LocalDate> dates) {
        User doctor = userService.getById(principal.getId());
        log.info("Getting busy slots of doctor with id {}", doctor.getId());
        List<DoctorSlot> slots = scheduleService.getBookedDoctorSlots(doctor, dates);
        return mapDoctorSlotListToScheduleDtoList(slots, dates);
    }

    /**
     * @param principal
     * @return List<ScheduleDto>
     * To get booking details for the patient
     */
    @GetMapping(value = "/my")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'ADMIN')")
    @JsonView({DtoView.PatientVisibility.class})
    public List<ScheduleDto> getPatientBookings(@AuthenticationPrincipal AwUserPrincipal principal) {
        User patient = userService.getById(principal.getId());
        log.info("Getting booking details for the patient with id {}", patient.getId());
        List<DoctorSlot> slots = scheduleService.getPatientBookings(patient);
        return mapDoctorSlotListToScheduleDtoList(slots);
    }

    /**
     * @param principal
     * @param bookSlotDto
     * @return DoctorSlotDto
     * To book slot for the patient
     */
    @PutMapping(value = "/book")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public DoctorSlotDto bookSlot(@AuthenticationPrincipal AwUserPrincipal principal, @RequestBody BookSlotDto bookSlotDto) {
        User patient = userService.getById(principal.getId());
        DoctorSlot slot;
        if (bookSlotDto.getSubscriptionId() != null && !bookSlotDto.getSubscriptionId().isEmpty()) {
            List<ProductSubscription> subscriptions = new ArrayList<>();
            bookSlotDto.getSubscriptionId().forEach(id -> subscriptions.add(subscriptionEngine.getById(id)));
            slot = scheduleService.bookAnAppointment(bookSlotDto.getSlotId(), patient, subscriptions);
        } else {
            slot = scheduleService.bookAnAppointment(bookSlotDto.getSlotId(), patient);
        }
        return parsingHelper.mapObject(slot, DoctorSlotDto.class);
    }

    /**
     * @param principal
     * @param slotId
     * @return DoctorSlotDto
     * To cancel booking appointment by patient
     */
    @PutMapping(value = "/cancel")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public DoctorSlotDto cancelBooking(@AuthenticationPrincipal AwUserPrincipal principal, @RequestBody long slotId) {
        User user = userService.getById(principal.getId());
        DoctorSlot slot = scheduleService.cancelAnAppointment(slotId, user);
        log.info("Appointment have been cancelled by patient with id {}", user.getId());
        return parsingHelper.mapObject(slot, DoctorSlotDto.class);
    }

    /**
     * @param principal
     * @param slotIdList
     * To delete their own time slots by doctor
     */
    @DeleteMapping(value = "/doctor/slot")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public void deleteSlot(@AuthenticationPrincipal AwUserPrincipal principal, @RequestBody List<Long> slotIdList) {
        User user = userService.getById(principal.getId());
        scheduleService.deleteSlot(user, slotIdList);
        log.info("Free time slots are deleted by doctor with id {}", user.getId());
    }

    /**
     * @param principal
     * @param schedule
     * @return List<ScheduleDto>
     * To create new schedule for the doctor
     */
    @PostMapping(value = "/doctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<ScheduleDto> createSchedule(@AuthenticationPrincipal AwUserPrincipal principal, @RequestBody CreateScheduleDto schedule) {
        User doctor = userService.getById(principal.getId());
        Set<LocalDate> dateSet = new TreeSet<>();
        List<TimeInterval> intervals = parsingHelper.mapObjects(schedule.getEvents(), TimeInterval.class);
        intervals.forEach(interval -> {
            dateSet.add(interval.getStart().toLocalDate());
            dateSet.add(interval.getEnd().toLocalDate());
        });
        List<LocalDate> dates = new LinkedList<>(dateSet);
        return mapDoctorSlotListToScheduleDtoList(scheduleService.createSchedule(doctor, intervals, schedule.isRecurring()), dates);
    }

    /**
     * @param slots
     * @param dates
     * @return List<ScheduleDto>
     * To map doctor's time slot with schedule list
     */
    private List<ScheduleDto> mapDoctorSlotListToScheduleDtoList(List<DoctorSlot> slots, List<LocalDate> dates) {
        List<ScheduleDto> result = new LinkedList<>();
        dates.forEach(date -> {
            ScheduleDto scheduleDto = new ScheduleDto();
            scheduleDto.setDate(date);
            slots.stream()
                    .filter(slot -> slot.getDate().equals(date))
                    .forEach(slot -> {
                        scheduleDto.getSchedule().putIfAbsent(slot.getTime(), new LinkedList<>());
                        scheduleDto.getSchedule().get(slot.getTime()).add(parsingHelper.mapObject(slot, DoctorSlotDto.class));
                    });
            result.add(scheduleDto);
        });
        return result;
    }

    /**
     * @param slots
     * @return List<ScheduleDto>
     * To map doctor's slot to schedule list
     */
    private List<ScheduleDto> mapDoctorSlotListToScheduleDtoList(List<DoctorSlot> slots) {
        Set<LocalDate> dateSet = new TreeSet<>();
        slots.forEach(slot -> dateSet.add(slot.getDate()));
        List<LocalDate> dates = dateSet.stream()
                .sorted(LocalDate::compareTo)
                .collect(Collectors.toList());
        return mapDoctorSlotListToScheduleDtoList(slots, dates);
    }
}
