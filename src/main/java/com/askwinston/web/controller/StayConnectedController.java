package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.StayConnectedRecord;
import com.askwinston.repository.StayConnectedRecordRepository;
import com.askwinston.service.UserService;
import com.askwinston.web.dto.DtoView;
import com.askwinston.web.dto.StayConnectedRecordDto;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stay-connected")
public class StayConnectedController {

    private StayConnectedRecordRepository repository;
    private UserService userService;
    private ParsingHelper parsingHelper;

    public StayConnectedController(StayConnectedRecordRepository repository, UserService userService, ParsingHelper parsingHelper) {
        this.repository = repository;
        this.userService = userService;
        this.parsingHelper = parsingHelper;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @JsonView(DtoView.AdminVisibility.class)
    public List<StayConnectedRecordDto> getAll() {
        List<StayConnectedRecord> records = new LinkedList<>();
        repository.findAll().forEach(records::add);
        records = records.stream().filter(record -> !userService.userEmailExists(record.getEmail())).collect(Collectors.toList());
        return parsingHelper.mapObjects(records, StayConnectedRecordDto.class);
    }

    @PostMapping
    public void saveEmail(@RequestBody StayConnectedRecordDto dto) {
        if (repository.findByEmail(dto.getEmail()) == null) {
            repository.save(parsingHelper.mapObject(dto, StayConnectedRecord.class));
        }
    }

    @DeleteMapping
    public void removeEmail(@RequestBody StayConnectedRecordDto dto) {
        if (repository.findByEmail(dto.getEmail()) != null) {
            repository.deleteByEmail(dto.getEmail());
        }
    }
}
