package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.ContactUsRecord;
import com.askwinston.notification.NotificationEngine;
import com.askwinston.repository.ContactUsRecordRepository;
import com.askwinston.web.dto.ContactUsDto;
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
    private ParsingHelper parsingHelper;

    public ContactUsController(ContactUsRecordRepository repository, NotificationEngine notificationEngine, ParsingHelper parsingHelper) {
        this.repository = repository;
        this.notificationEngine = notificationEngine;
        this.parsingHelper = parsingHelper;
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
    public void save(@RequestBody ContactUsDto record) {
        ContactUsRecord contactUsRecord = repository.save(parsingHelper.mapObject(record, ContactUsRecord.class));
        notificationEngine.notify(CONTACT_US_REQUEST_SAVED, contactUsRecord);
    }

    /**
     * @param record
     * To delete existing contact us record
     */
    @DeleteMapping
    public void remove(@RequestBody ContactUsDto record) {
        repository.delete(parsingHelper.mapObject(record, ContactUsRecord.class));
    }
}
