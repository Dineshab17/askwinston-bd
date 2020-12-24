package com.askwinston.helper;

import com.askwinston.exception.NotFoundException;
import com.askwinston.model.*;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.repository.NotificationTemplateRepository;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.ProductSubscriptionDto;
import com.askwinston.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
    Class that helps convert dto-entities to
    entities stored in database and back
 */

@Slf4j
public class ParsingHelper {

    private NotificationTemplateRepository notificationTemplateRepository;
    private ModelMapper modelMapper;

    @Autowired
    public ParsingHelper(ModelMapper modelMapper,
                         NotificationTemplateRepository notificationTemplateRepository) {
        this.modelMapper = modelMapper;
        this.notificationTemplateRepository = notificationTemplateRepository;
    }

    public <T> T mapObject(Object source, Class<T> resultType) {
        return modelMapper.map(source, resultType);
    }

    public <T> List<T> mapObjects(List<?> sourceList, Class<T> resultType) {
        return sourceList.stream()
                .map(obj -> mapObject(obj, resultType))
                .collect(Collectors.toList());
    }

    @PostConstruct
    public void setupMapper() {
        modelMapper.createTypeMap(Notification.class, NotificationDto.class)
                .addMappings(m -> m.skip(NotificationDto::setNotificationTemplate))
                .setPostConverter(toDtoNotificationConverter());

        modelMapper.createTypeMap(NotificationDto.class, Notification.class)
                .addMappings(m -> m.skip(Notification::setTemplate))
                .setPostConverter(toEntityNotificationConverter());

        modelMapper.createTypeMap(NotificationTemplateDto.class, NotificationTemplate.class)
                .addMappings(m -> m.skip(NotificationTemplate::setNotificationEventType))
                .setPostConverter(toEntityNotificationTemplateConverter());

        modelMapper.createTypeMap(NotificationTemplatePersistenceDto.class, NotificationTemplate.class)
                .addMappings(m -> m.skip(NotificationTemplate::setNotificationEventType))
                .setPostConverter(toEntityNotificationTemplatePersistenceConverter());

        modelMapper.createTypeMap(NotificationTemplate.class, NotificationTemplatePersistenceDto.class)
                .addMappings(m -> m.skip(NotificationTemplatePersistenceDto::setNotificationEventTypeName))
                .setPostConverter(toDtoNotificationTemplatePersistenceConverter());

        modelMapper.createTypeMap(Notification.class, NotificationPersistenceDto.class)
                .addMappings(m -> m.skip(NotificationPersistenceDto::setTemplate))
                .setPostConverter(toDtoNotificationPersistenceConverter());

        modelMapper.createTypeMap(NotificationPersistenceDto.class, Notification.class)
                .addMappings(m -> m.skip(Notification::setTemplate))
                .setPostConverter(toEntityNotificationPersistenceConverter());

        modelMapper.createTypeMap(User.class, UserDto.class)
                .addMappings(m -> m.skip(UserDto::setIdDocument))
                .addMappings(m -> m.skip(UserDto::setInsuranceDocument))
                .addMappings(m -> m.skip(UserDto::setPassword))
                .addMappings(m -> m.skip(UserDto::setBillingCards))
                .setPostConverter(toDtoUserConverter());

        modelMapper.createTypeMap(Product.class, ProductDto.class)
                .addMappings(m -> m.skip(ProductDto::setQuantities))
                .setPostConverter(toDtoProductConverter());

        modelMapper.createTypeMap(ProductSubscription.class, ProductSubscriptionDto.class)
                .addMappings(m -> m.skip(ProductSubscriptionDto::setBillingInfo))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setShippingAddressCity))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setShippingAddressCountry))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setShippingAddressLine1))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setShippingAddressLine2))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setShippingAddressProvince))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setShippingAddressPostalCode))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setUser))
                .addMappings(m -> m.skip(ProductSubscriptionDto::setDate))
                .setPostConverter(toDtoProductSubscriptionConverter());

        modelMapper.createTypeMap(PurchaseOrder.class, PurchaseOrderDto.class)
                .addMappings(m -> m.skip(PurchaseOrderDto::setUser))
                .addMappings(m -> m.skip(PurchaseOrderDto::setRxDocumentId))
                .addMappings(m -> m.skip(PurchaseOrderDto::setRxTransferNumber))
                .addMappings(m -> m.skip(PurchaseOrderDto::setPharmacyNameAndAddress))
                .addMappings(m -> m.skip(PurchaseOrderDto::setPharmacyPhone))
                .setPostConverter(toDtoPurchaseOrderConverter());

        modelMapper.createTypeMap(RxTransferStateRecord.class, RxTransferStateRecordDto.class)
                .addMappings(m -> m.skip(RxTransferStateRecordDto::setProduct))
                .addMappings(m -> m.skip(RxTransferStateRecordDto::setProductCategory))
                .addMappings(m -> m.skip(RxTransferStateRecordDto::setQuantity))
                .setPostConverter(toDtoRxTransferStateRecordConverter());

        modelMapper.createTypeMap(DoctorSlot.class, DoctorSlotDto.class)
                .addMappings(m -> m.skip(DoctorSlotDto::setDoctor))
                .addMappings(m -> m.skip(DoctorSlotDto::setSubscription))
                .setPostConverter(toDtoDoctorSlotConverter());

        modelMapper.createTypeMap(TimeIntervalDto.class, TimeInterval.class)
                .addMappings(m -> m.skip(TimeInterval::setStart))
                .addMappings(m -> m.skip(TimeInterval::setEnd))
                .setPostConverter(toEntityTimeIntervalConverter());
    }

    private Converter<NotificationTemplateDto, NotificationTemplate> toEntityNotificationTemplateConverter() {
        return context -> {
            NotificationTemplateDto source = context.getSource();
            NotificationTemplate destination = context.getDestination();
            destination.setNotificationEventType(NotificationEventTypeContainer.valueOf(source.getNotificationEventType().getName()));
            return context.getDestination();
        };
    }

    private Converter<NotificationTemplatePersistenceDto, NotificationTemplate> toEntityNotificationTemplatePersistenceConverter() {
        return context -> {
            NotificationTemplatePersistenceDto source = context.getSource();
            NotificationTemplate destination = context.getDestination();
            destination.setNotificationEventType(NotificationEventTypeContainer.valueOf(source.getNotificationEventTypeName()));
            return context.getDestination();
        };
    }

    private Converter<NotificationTemplate, NotificationTemplatePersistenceDto> toDtoNotificationTemplatePersistenceConverter() {
        return context -> {
            NotificationTemplate source = context.getSource();
            NotificationTemplatePersistenceDto destination = context.getDestination();
            destination.setNotificationEventTypeName(source.getNotificationEventType().getName());
            return context.getDestination();
        };
    }

    private Converter<NotificationDto, Notification> toEntityNotificationConverter() {
        return context -> {
            NotificationDto source = context.getSource();
            Notification destination = context.getDestination();

            try {
                NotificationTemplatePersistenceDto dto = notificationTemplateRepository.findByName(source.getNotificationTemplate())
                        .orElseThrow(() -> new NotFoundException("NotificationTemplate not found"));
                destination.setTemplate(mapObject(dto, NotificationTemplate.class));
            } catch (NotFoundException e) {
                log.error(e.getMessage());
            }
            return context.getDestination();
        };
    }

    private Converter<Notification, NotificationDto> toDtoNotificationConverter() {
        return context -> {
            Notification source = context.getSource();
            NotificationDto destination = context.getDestination();
            destination.setNotificationTemplate(source.getTemplate().getName());
            return context.getDestination();
        };
    }

    private Converter<NotificationPersistenceDto, Notification> toEntityNotificationPersistenceConverter() {
        return context -> {
            NotificationPersistenceDto source = context.getSource();
            Notification destination = context.getDestination();
            try {
                NotificationTemplatePersistenceDto dto = notificationTemplateRepository.findByName(source.getTemplate().getNotificationEventTypeName())
                        .orElseThrow(() -> new NotFoundException("NotificationTemplate not found"));
                destination.setTemplate(mapObject(dto, NotificationTemplate.class));
            } catch (NotFoundException e) {
                log.error(e.getMessage());
            }
            return context.getDestination();
        };
    }

    private Converter<Notification, NotificationPersistenceDto> toDtoNotificationPersistenceConverter() {
        return context -> {
            Notification source = context.getSource();
            NotificationPersistenceDto destination = context.getDestination();
            destination.setTemplate(mapObject(source.getTemplate(), NotificationTemplatePersistenceDto.class));
            return context.getDestination();
        };
    }

    private Converter<ProductSubscription, ProductSubscriptionDto> toDtoProductSubscriptionConverter() {
        return context -> {
            ProductSubscription source = context.getSource();
            ProductSubscriptionDto destination = context.getDestination();
            ShippingAddress address = source.getShippingAddress();
            destination.setShippingAddressLine1(address.getAddressLine1());
            destination.setShippingAddressLine2(address.getAddressLine2());
            destination.setShippingAddressCity(address.getAddressCity());
            destination.setShippingAddressProvince(address.getAddressProvince().getName());
            destination.setShippingAddressPostalCode(address.getAddressPostalCode());
            destination.setShippingAddressCountry(address.getAddressCountry());
            if (source.getBillingCard() != null) {
                destination.setBillingInfo("**** **** **** " + source.getBillingCard().getLast4());
            }
            LocalDateTime date = source.getCreationDate();
            if (date == null) {
                date = source.getDate().atStartOfDay();
            }
            destination.setDate(date);
            User user = source.getUser();
            UserDto userDto = mapObject(user, UserDto.class);
            destination.setUser(userDto);
            return context.getDestination();
        };
    }

    private Converter<User, UserDto> toDtoUserConverter() {
        return context -> {
            User source = context.getSource();
            UserDto destination = context.getDestination();
            Document document = source.getIdDocument();
            if (document != null) {
                destination.setIdDocument(source.getIdDocument().getId());
            } else {
                destination.setIdDocument(null);
            }
            document = source.getInsuranceDocument();
            if (document != null) {
                destination.setInsuranceDocument(source.getInsuranceDocument().getId());
            } else {
                destination.setInsuranceDocument(null);
            }
            List<BillingCard> cards = source.getBillingCards().stream()
                    .filter(BillingCard::getIsValid)
                    .collect(Collectors.toList());
            destination.setBillingCards(mapObjects(cards, BillingCardDto.class));
            return context.getDestination();
        };
    }

    private Converter<Product, ProductDto> toDtoProductConverter() {
        return context -> {
            Product source = context.getSource();
            ProductDto destination = context.getDestination();
            List<ProductQuantity> quantities = source.getQuantities();
            quantities.sort(Comparator.comparing(ProductQuantity::getOrdinal));
            destination.setQuantities(mapObjects(quantities, ProductQuantityDto.class));
            return context.getDestination();
        };
    }

    private Converter<PurchaseOrder, PurchaseOrderDto> toDtoPurchaseOrderConverter() {
        return context -> {
            PurchaseOrder source = context.getSource();
            PurchaseOrderDto destination = context.getDestination();
            User user = source.getUser();
            UserDto userDto = mapObject(user, UserDto.class);
            if (source.getStatus().equals(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK)) {
                destination.setPharmacyPhone(source.getPrescription().getPharmacyPhone());
                destination.setPharmacyNameAndAddress(source.getPrescription().getPharmacyNameAndAddress());
                destination.setRxTransferNumber(source.getPrescription().getRxTransferNumber());
                destination.setRxDocumentId(source.getPrescription().getRxDocument().getId());
                destination.setMaxRefillsNumber(source.getSubscription().getTotalRefills());
            }
            if (source.getCourier() != null) {
                destination.setCourier(source.getCourier().getName());
            } else {
                destination.setCourier(Courier.CANADA_POST.getName());
            }
            destination.setUser(userDto);
            return context.getDestination();
        };
    }

    private Converter<RxTransferStateRecord, RxTransferStateRecordDto> toDtoRxTransferStateRecordConverter() {
        return context -> {
            RxTransferStateRecord source = context.getSource();
            RxTransferStateRecordDto destination = context.getDestination();
            if (source.getProductLabel() != null && source.getProductValue() != null)
                destination.setProduct(new Option(source.getProductLabel(), source.getProductValue()));
            if (source.getProductCategoryLabel() != null && source.getProductCategoryValue() != null)
                destination.setProductCategory(new Option(source.getProductCategoryLabel(), source.getProductCategoryValue()));
            if (source.getQuantityLabel() != null && source.getQuantityValue() != null)
                destination.setQuantity(new Option(source.getQuantityLabel(), source.getQuantityValue()));
            return context.getDestination();
        };
    }

    private Converter<DoctorSlot, DoctorSlotDto> toDtoDoctorSlotConverter() {
        return context -> {
            DoctorSlot source = context.getSource();
            DoctorSlotDto destination = context.getDestination();
            destination.setSubscription(mapObjects(source.getSubscriptions(), ProductSubscriptionDto.class));
            destination.setDoctor(source.getDoctor().getFirstName() + " " + source.getDoctor().getLastName());
            return context.getDestination();
        };
    }

    private Converter<TimeIntervalDto, TimeInterval> toEntityTimeIntervalConverter() {
        return context -> {
            TimeIntervalDto source = context.getSource();
            TimeInterval destination = context.getDestination();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            destination.setStart(LocalDateTime.parse(source.getStart(), formatter));
            destination.setEnd(LocalDateTime.parse(source.getEnd(), formatter));
            return context.getDestination();
        };
    }
}