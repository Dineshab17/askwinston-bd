package com.askwinston.service;

import com.askwinston.web.dto.RxTransferStateRecordDto;

public interface RxTransferService {

    RxTransferStateRecordDto saveRxTransferState(RxTransferStateRecordDto stateRecord, Long userId);

    RxTransferStateRecordDto getRxTransferStateByUser(Long userId);

    void deleteRxTransferStateByUser(Long userId);
}
