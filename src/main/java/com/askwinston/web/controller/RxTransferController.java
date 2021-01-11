package com.askwinston.web.controller;

import com.askwinston.service.RxTransferService;
import com.askwinston.web.dto.DtoView;
import com.askwinston.web.dto.RxTransferStateRecordDto;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rx-transfer")
public class RxTransferController {

    private RxTransferService rxTransferService;

    public RxTransferController(RxTransferService rxTransferService) {
        this.rxTransferService = rxTransferService;
    }

    /**
     * @param dto
     * @param principal
     * @return RxTransferStateRecordDto
     * To save prescription state of the patient
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public RxTransferStateRecordDto saveRxTransferState(@RequestBody RxTransferStateRecordDto dto,
                                                        @AuthenticationPrincipal AwUserPrincipal principal) {
        return rxTransferService.saveRxTransferState(dto, principal.getId());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public RxTransferStateRecordDto getRxTransferState(@AuthenticationPrincipal AwUserPrincipal principal) {
        return rxTransferService.getRxTransferStateByUser(principal.getId());
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public void deleteRxTransferState(@AuthenticationPrincipal AwUserPrincipal principal) {
        rxTransferService.deleteRxTransferStateByUser(principal.getId());
    }
}
