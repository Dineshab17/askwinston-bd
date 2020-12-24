package com.askwinston.repository;

import com.askwinston.model.DoctorSlot;
import com.askwinston.model.ScheduleScheme;
import com.askwinston.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface DoctorSlotRepository extends CrudRepository<DoctorSlot, Long> {

    List<DoctorSlot> findAllByDoctorAndDateInOrderByDate(User doctor, Collection<LocalDate> date);

    List<DoctorSlot> findAllByDoctorAndDateInAndPatientIsNullOrderByDate(User doctor, Collection<LocalDate> date);

    List<DoctorSlot> findAllByDoctorAndDateInAndPatientIsNotNullOrderByDate(User doctor, Collection<LocalDate> date);

    List<DoctorSlot> findAllByIdInAndPatientIsNull(Collection<Long> id);

    List<DoctorSlot> findAllByScheme(ScheduleScheme scheduleScheme);

    List<DoctorSlot> findAllByPatientOrderByDate(User patient);

    @Query(value = "select ds.* from doctor_slot ds\n" +
            "       inner join user u on u.authority = 'DOCTOR' and u.specialisation in :specialisations\n" +
            "       where ds.date in :date and ds.is_free = true", nativeQuery = true)
    List<DoctorSlot> findAllAvailableByDoctorSpecialisationAndDateInOrderByDate(Collection<String> specialisations, Collection<String> date);

}
