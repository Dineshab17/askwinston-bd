package com.askwinston.web.controller;

import com.askwinston.model.ContactUsRecord;
import com.askwinston.notification.NotificationEngine;
import com.askwinston.repository.ContactUsRecordRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

import static com.askwinston.notification.NotificationEventTypeContainer.CONTACT_US_REQUEST_SAVED;

@RestController
@RequestMapping("/contact-us-record")
public class ContactUsController {

    private ContactUsRecordRepository repository;
    private NotificationEngine notificationEngine;

    public ContactUsController(ContactUsRecordRepository repository, NotificationEngine notificationEngine) {
        this.repository = repository;
        this.notificationEngine = notificationEngine;
    }

    /**
     * @return List<ContactUsRecord>
     * To get all the contact us records
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<ContactUsRecord> getAll() {
        List<ContactUsRecord> list = new LinkedList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    /**
     * @param record
     * To save new contact us record
     */
    @PostMapping
    public void save(@RequestBody ContactUsRecord record) {
        record = repository.save(record);
        notificationEngine.notify(CONTACT_US_REQUEST_SAVED, record);
    }

    /**
     * @param record
     * To delete existing contact us record
     */
    @DeleteMapping
    public void remove(@RequestBody ContactUsRecord record) {
        repository.delete(record);
    }
}
