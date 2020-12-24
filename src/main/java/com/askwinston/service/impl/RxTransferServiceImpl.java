package com.askwinston.service.impl;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.RxTransferStateRecord;
import com.askwinston.model.User;
import com.askwinston.repository.RxTransferStatementRecordRepository;
import com.askwinston.service.RxTransferService;
import com.askwinston.service.UserService;
import com.askwinston.web.dto.RxTransferStateRecordDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RxTransferServiceImpl implements RxTransferService {

    private RxTransferStatementRecordRepository recordRepository;
    private UserService userService;
    private ParsingHelper parsingHelper;

    public RxTransferServiceImpl(RxTransferStatementRecordRepository recordRepository,
                                 ParsingHelper parsingHelper,
                                 UserService userService) {
        this.recordRepository = recordRepository;
        this.parsingHelper = parsingHelper;
        this.userService = userService;
    }

    @Override
    @Transactional
    public RxTransferStateRecordDto saveRxTransferState(RxTransferStateRecordDto dto, Long userId) {
        User user = userService.getById(userId);
        RxTransferStateRecord record = recordRepository.findByUserId(user.getId()).orElse(null);
        if (record == null) {
            record = RxTransferStateRecord.builder()
                    .userId(userId)
                    .step(dto.getStep())
                    .build();
            record = recordRepository.save(record);
        }
        record.setStep(dto.getStep());
        if (dto.getPharmacyName() != null) {
            record.setPharmacyName(dto.getPharmacyName());
        }
        if (dto.getRxNumber() != null) {
            record.setRxNumber(dto.getRxNumber());
        }
        if (dto.getPharmacyPhone() != null) {
            record.setPharmacyPhone(dto.getPharmacyPhone());
        }
        if (dto.getBottleImageId() != null) {
            record.setBottleImageId(dto.getBottleImageId());
        }
        if (dto.getProduct() != null) {
            record.setProductLabel(dto.getProduct().getLabel());
            record.setProductValue(dto.getProduct().getValue());
        }
        if (dto.getProductCategory() != null) {
            record.setProductCategoryLabel(dto.getProductCategory().getLabel());
            record.setProductCategoryValue(dto.getProductCategory().getValue());
        }
        if (dto.getQuantity() != null) {
            record.setQuantityLabel(dto.getQuantity().getLabel());
            record.setQuantityValue(dto.getQuantity().getValue());
        }
        return parsingHelper.mapObject(recordRepository.save(record), RxTransferStateRecordDto.class);
    }

    @Override
    @Transactional
    public RxTransferStateRecordDto getRxTransferStateByUser(Long userId) {
        User user = userService.getById(userId);
        RxTransferStateRecord result = recordRepository.findByUserId(user.getId()).orElse(null);
        return result == null ? null : parsingHelper.mapObject(result, RxTransferStateRecordDto.class);
    }

    @Override
    @Transactional
    public void deleteRxTransferStateByUser(Long userId) {
        User user = userService.getById(userId);
        recordRepository.deleteByUserId(user.getId());
    }
}
