package com.askwinston.service.impl;

import com.askwinston.helper.TransactionTemplateBuilder;
import com.askwinston.model.*;
import com.askwinston.notification.NotificationEngine;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.repository.DoctorSlotRepository;
import com.askwinston.repository.ScheduleSchemeRepository;
import com.askwinston.service.ScheduleService;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.ProductSubscriptionRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private static final int APPOINTMENT_DURATION = 15; //In minutes
    private static final int GENERATE_SCHEDULE_FOR = 5; //In weeks

    private ProductSubscriptionRepository subscriptionRepository;
    private DoctorSlotRepository doctorSlotRepository;
    private ScheduleSchemeRepository scheduleSchemeRepository;
    private NotificationEngine notificationEngine;
    private TransactionTemplateBuilder templateBuilder;

    public ScheduleServiceImpl(ProductSubscriptionRepository subscriptionRepository, DoctorSlotRepository doctorSlotRepository, ScheduleSchemeRepository scheduleSchemeRepository, NotificationEngine notificationEngine, TransactionTemplateBuilder templateBuilder) {
        this.subscriptionRepository = subscriptionRepository;
        this.doctorSlotRepository = doctorSlotRepository;
        this.scheduleSchemeRepository = scheduleSchemeRepository;
        this.notificationEngine = notificationEngine;
        this.templateBuilder = templateBuilder;
    }

    /**
     * Cron job to schedule time slot for doctors on every monday
     */
    @Scheduled(cron = "${notification.cron:0 0 0 ? * MON}")
    private void generateSchedules() {
        templateBuilder.requiresNew().execute(session -> {
            List<ScheduleScheme> schemes = new ArrayList<>();
            scheduleSchemeRepository.findAll().forEach(schemes::add);
            schemes.forEach(this::generateScheduleFromScheme);
            return null;
        });
    }

    /**
     * @param doctorSlotId
     * @return DoctorSlot
     * To get doctor slot by id
     */
    @Override
    public DoctorSlot getById(long doctorSlotId) {
        return doctorSlotRepository.findById(doctorSlotId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor slot not found")
        );
    }

    /**
     * @param doctor
     * @param dates
     * @return List<DoctorSlot>
     * To get all slots of doctor
     */
    @Override
    public List<DoctorSlot> getAllDoctorSlots(User doctor, List<LocalDate> dates) {
        return doctorSlotRepository.findAllByDoctorAndDateInOrderByDate(doctor, dates);
    }

    /**
     * @param doctor
     * @param dates
     * @return List<DoctorSlot>
     * To get available free slots of doctor
     */
    @Override
    public List<DoctorSlot> getAvailableDoctorSlots(User doctor, List<LocalDate> dates) {
        return doctorSlotRepository.findAllByDoctorAndDateInAndPatientIsNullOrderByDate(doctor, dates);
    }

    /**
     * @param doctor
     * @param dates
     * @return List<DoctorSlot>
     * To get booked or busy time slots of doctor
     */
    @Override
    public List<DoctorSlot> getBookedDoctorSlots(User doctor, List<LocalDate> dates) {
        return doctorSlotRepository.findAllByDoctorAndDateInAndPatientIsNotNullOrderByDate(doctor, dates);
    }

    /**
     * @param category
     * @param dates
     * @return List<DoctorSlot>
     * To get available time slots of doctor based on specialization
     */
    @Override
    public List<DoctorSlot> getAvailableSlotsByProblemCategory(Product.ProblemCategory category, List<LocalDate> dates) {
        List<User.DoctorSpecialisation> specialisations = Arrays.stream(User.DoctorSpecialisation.values())
                .filter(s -> Arrays.asList(s.getCategories()).contains(category))
                .collect(Collectors.toList());
        List<String> datesStrings = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
        List<String> specialisationsStrings = specialisations.stream()
                .map(Enum::toString)
                .collect(Collectors.toList());
        return doctorSlotRepository.findAllAvailableByDoctorSpecialisationAndDateInOrderByDate(specialisationsStrings, datesStrings);
    }

    /**
     * @param patient
     * @return List<DoctorSlot>
     * To get booking details for the patient
     */
    @Override
    public List<DoctorSlot> getPatientBookings(User patient) {
        return doctorSlotRepository.findAllByPatientOrderByDate(patient);
    }

    /**
     * @param doctorSlotId
     * @param patient
     * @return DoctorSlot
     * To book an appointment for the patient
     */
    @Override
    public DoctorSlot bookAnAppointment(long doctorSlotId, User patient) {
        DoctorSlot slot = getById(doctorSlotId);
        slot.setPatient(patient);
        slot.setFree(false);
        return doctorSlotRepository.save(slot);
    }

    /**
     * @param doctorSlotId
     * @param patient
     * @param subscriptions
     * @return DoctorSlot
     * To book an appointment for the patient
     */
    @Override
    public DoctorSlot bookAnAppointment(long doctorSlotId, User patient, List<ProductSubscription> subscriptions) {
        return templateBuilder.requiresNew().execute(session -> {
            DoctorSlot slot = bookAnAppointment(doctorSlotId, patient);
            slot.getSubscriptions().addAll(subscriptions);
            slot = doctorSlotRepository.save(slot);
            DoctorSlot finalSlot = slot;
            subscriptions.forEach(s -> s.setAppointment(finalSlot));
            subscriptionRepository.saveAll(subscriptions);
            notificationEngine.notify(NotificationEventTypeContainer.NEW_APPOINTMENT, slot);
            slot = doctorSlotRepository.save(slot);
            Hibernate.initialize(slot.getSubscriptions());
            return slot;
        });
    }

    /**
     * @param doctorSlotId
     * @param user
     * @return doctorSlot
     * To cancel doctor's appointment by patient
     */
    @Override
    public DoctorSlot cancelAnAppointment(long doctorSlotId, User user) {
        DoctorSlot slot = getById(doctorSlotId);
        if (!slot.getPatient().equals(user) && !user.getAuthority().equals(User.Authority.PATIENT)) {
            log.error("User with id {} not permitted to cancel an appointment", user.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not permitted");
        }
        slot.setPatient(null);
        slot.setFree(true);
        List<ProductSubscription> subscriptions = slot.getSubscriptions();
        subscriptions.forEach(s -> s.setAppointment(null));
        subscriptionRepository.saveAll(subscriptions);
        return doctorSlotRepository.save(slot);
    }

    /**
     * @param doctor
     * @param intervals
     * @param isRecurring
     * @return List<DoctorSlot>
     * To create new schedule or time slot for the doctor
     */
    @Override
    public List<DoctorSlot> createSchedule(User doctor, List<TimeInterval> intervals, boolean isRecurring) {
        if (isRecurring) {
            LocalDate startDate = intervals.stream()
                    .map(i -> i.getStart().toLocalDate()).min(LocalDate::compareTo)
                    .orElseThrow(IllegalArgumentException::new);
            ScheduleScheme scheduleScheme = generateScheduleScheme(doctor, intervals, startDate);
            return generateScheduleFromScheme(scheduleScheme, startDate);
        } else {
            return createScheduleFromIntervals(doctor, intervals);
        }
    }

    /**
     * @param user
     * @param slotIdList
     * To delete slot by the doctor
     */
    @Override
    public void deleteSlot(User user, List<Long> slotIdList) {
        List<DoctorSlot> slots = doctorSlotRepository.findAllByIdInAndPatientIsNull(slotIdList);
        boolean areSlotsBelongToThisUser = slots.stream()
                .allMatch(slot -> slot.getDoctor().equals(user));
        if (!user.getAuthority().equals(User.Authority.ADMIN) && (!user.getAuthority().equals(User.Authority.DOCTOR) || !areSlotsBelongToThisUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This orders aren't yours");
        }
        slots = slots.stream()
                .filter(slot -> (slot.getPatient() == null) && (slot.isFree()))
                .collect(Collectors.toList());
        doctorSlotRepository.deleteAll(slots);
    }

    /**
     * @param doctor
     * @param intervals
     * @return List<DoctorSlot>
     * To create doctor schedule from given intervals
     */
    private List<DoctorSlot> createScheduleFromIntervals(User doctor, List<TimeInterval> intervals) {
        List<DoctorSlot> newSlots = new ArrayList<>();
        intervals.forEach(interval -> {
            LocalDateTime dateTime = interval.getStart();
            long minutes = interval.getStart().until(interval.getEnd(), ChronoUnit.MINUTES);
            long slotCount = minutes / APPOINTMENT_DURATION;
            for (long i = 0; i < slotCount; i++) {
                DoctorSlot slot = new DoctorSlot();
                slot.setDate(dateTime.toLocalDate());
                slot.setTime(dateTime.toLocalTime());
                slot.setDoctor(doctor);
                slot.setFree(true);
                newSlots.add(slot);
                dateTime = dateTime.plusMinutes(APPOINTMENT_DURATION);
            }
        });
        return saveDoctorSlots(doctor, newSlots);
    }

    /**
     * @param doctor
     * @param slots
     * @return List<DoctorSlot>
     * To save list of slots for the doctor
     */
    private List<DoctorSlot> saveDoctorSlots(User doctor, List<DoctorSlot> slots) {
        Set<LocalDate> dateSet = new HashSet<>();
        slots.forEach(slot -> dateSet.add(slot.getDate()));
        List<LocalDate> dates = dateSet.stream()
                .sorted(LocalDate::compareTo)
                .collect(Collectors.toList());
        List<DoctorSlot> existingSlots = doctorSlotRepository.findAllByDoctorAndDateInOrderByDate(doctor, dates);
        Set<DoctorSlot> resultSet = new TreeSet<>((o1, o2) -> {
            if (o1.getDate().compareTo(o2.getDate()) != 0) {
                return o1.getDate().compareTo(o2.getDate());
            } else {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        resultSet.addAll(existingSlots);
        resultSet.addAll(slots);
        doctorSlotRepository.saveAll(resultSet);
        return Lists.newArrayList(resultSet);
    }

    /**
     * @param doctor
     * @param intervals
     * @param startDate
     * @return ScheduleScheme
     * To generate new schedule scheme for the doctor
     */
    private ScheduleScheme generateScheduleScheme(User doctor, List<TimeInterval> intervals, LocalDate startDate) {
        Optional<ScheduleScheme> oldScheduleScheme = scheduleSchemeRepository.findByDoctor(doctor);
        final ScheduleScheme scheduleScheme = scheduleSchemeRepository.save(new ScheduleScheme());
        scheduleScheme.setDoctor(doctor);
        Set<LocalDate> dateSet = new HashSet<>();
        intervals.forEach(interval -> {
            dateSet.add(interval.getStart().toLocalDate());
            dateSet.add(interval.getEnd().toLocalDate());
        });
        if (dateSet.size() > 7) {
            throw new IllegalArgumentException("Schedule must contains maximum 7 dates");
        }
        List<LocalDate> dates = dateSet.stream()
                .sorted(LocalDate::compareTo)
                .collect(Collectors.toList());
        dates.forEach(date ->
            intervals.stream()
                    .filter(interval -> interval.getStart().toLocalDate().equals(date))
                    .forEach(interval -> {
                        ScheduleInterval scheduleInterval = new ScheduleInterval();
                        scheduleInterval.setDayOfWeek(interval.getStart().getDayOfWeek());
                        scheduleInterval.setStart(interval.getStart().toLocalTime());
                        scheduleInterval.setEnd(interval.getEnd().toLocalTime());
                        scheduleInterval.setScheduleScheme(scheduleScheme);
                        scheduleScheme.getIntervals().add(scheduleInterval);
                    })
        );
        oldScheduleScheme.ifPresent(scheme -> deleteScheduleScheme(scheme, startDate));
        return scheduleSchemeRepository.save(scheduleScheme);
    }

    private List<DoctorSlot> generateScheduleFromScheme(ScheduleScheme scheduleScheme, LocalDate startDate) {
        List<DoctorSlot> newSlots = new ArrayList<>();
        int currentDayOrdinal = startDate.getDayOfWeek().getValue();
        LocalDate firstDayOfThePeriod = startDate.minusDays(currentDayOrdinal - 1L);
        LocalDate lastDayOfThePeriod = firstDayOfThePeriod.plusWeeks(GENERATE_SCHEDULE_FOR).plusDays(1);
        LocalDate currentDate = firstDayOfThePeriod;
        while (!currentDate.isAfter(lastDayOfThePeriod)) {
            if (!currentDate.isBefore(startDate)) {
                final LocalDate finalDate = currentDate;
                scheduleScheme.getIntervals().stream()
                        .filter(interval -> interval.getDayOfWeek().equals(finalDate.getDayOfWeek()))
                        .forEach(interval -> {
                            LocalTime currentTime = interval.getStart();
                            while (currentTime.isBefore(interval.getEnd())) {
                                DoctorSlot newSlot = new DoctorSlot();
                                newSlot.setDate(finalDate);
                                newSlot.setTime(currentTime);
                                newSlot.setFree(true);
                                newSlot.setDoctor(scheduleScheme.getDoctor());
                                newSlot.setScheme(scheduleScheme);
                                newSlots.add(newSlot);
                                currentTime = currentTime.plusMinutes(APPOINTMENT_DURATION);
                            }
                        });
            }
            currentDate = currentDate.plusDays(1);
        }
        return saveDoctorSlots(scheduleScheme.getDoctor(), newSlots);
    }

    private void generateScheduleFromScheme(ScheduleScheme scheduleScheme) {
        generateScheduleFromScheme(scheduleScheme, LocalDate.now());
    }

    private void deleteScheduleScheme(ScheduleScheme scheduleScheme, LocalDate fromDate) {
        List<DoctorSlot> existingSlots = doctorSlotRepository.findAllByScheme(scheduleScheme);
        existingSlots.stream()
                .forEach(s -> s.setScheme(null));
        doctorSlotRepository.saveAll(existingSlots);
        doctorSlotRepository.deleteAll(existingSlots.stream().filter(slot -> !slot.getDate().isBefore(fromDate)).filter(DoctorSlot::isFree).collect(Collectors.toList()));
        scheduleSchemeRepository.delete(scheduleScheme);
    }
}
