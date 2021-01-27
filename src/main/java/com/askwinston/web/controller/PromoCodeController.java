package com.askwinston.web.controller;

import com.askwinston.exception.PromoCodeException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Product;
import com.askwinston.model.PromoCode;
import com.askwinston.repository.ProductRepository;
import com.askwinston.repository.PromoCodeRepository;
import com.askwinston.service.PromoCodeService;
import com.askwinston.web.dto.PromoCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/promo-code")
@Slf4j
public class PromoCodeController {

    private PromoCodeService promoCodeService;
    private PromoCodeRepository promoCodeRepository;
    private ParsingHelper parsingHelper;
    private ProductRepository productRepository;

    public PromoCodeController(PromoCodeService promoCodeService, PromoCodeRepository promoCodeRepository, ParsingHelper parsingHelper, ProductRepository productRepository) {
        this.promoCodeService = promoCodeService;
        this.promoCodeRepository = promoCodeRepository;
        this.parsingHelper = parsingHelper;
        this.productRepository = productRepository;
    }

    /**
     * @return List<PromoCodeDto>
     * To get all the Promo codes created by admin
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<PromoCodeDto> getAll() {
        List<PromoCode> list = new LinkedList<>();
        log.info("Getting all PromoCodes");
        promoCodeRepository.findAll().forEach(list::add);
        return parsingHelper.mapObjects(list, PromoCodeDto.class);
    }

    /**
     * @return List<Product.ProblemCategory>
     * To get categories for each product
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Product.ProblemCategory> getCategories() {
        Set<Product.ProblemCategory> categories = new HashSet<>();
        log.info("Getting categories for each product");
        productRepository.findAll().forEach(product -> categories.add(product.getProblemCategory()));
        return new ArrayList<>(categories);
    }

    /**
     * @param promoCodeDto
     * @return PromoCodeDto
     * To create new PromoCode by admin
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public PromoCodeDto create(@RequestBody PromoCodeDto promoCodeDto) {
        PromoCode promoCode = promoCodeService.savePromoCode(promoCodeDto.getCode(), promoCodeDto.getFromDate(), promoCodeDto.getToDate(), promoCodeDto.getValue(), promoCodeDto.getProblemCategory(), promoCodeDto.getType());
        log.info("PromoCode created with id {}", promoCode.getId());
        return parsingHelper.mapObject(promoCode, PromoCodeDto.class);
    }

    /**
     * @param code
     * To delete provided PromoCode
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void delete(@RequestBody String code) {
        promoCodeService.deletePromoCode(code);
    }

    /**
     * @param code
     * @return PromoCodeDto
     * To verify Promo Code exists
     */
    @GetMapping("/{code}")
    public PromoCodeDto verifyPromoCode(@PathVariable("code") String code) {
        try {
            return parsingHelper.mapObject(promoCodeService.getByCode(code), PromoCodeDto.class);
        } catch (PromoCodeException e) {
            log.error("Error verifying promo code {}", code);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
