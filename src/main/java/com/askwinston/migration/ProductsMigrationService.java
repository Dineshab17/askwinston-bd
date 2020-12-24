package com.askwinston.migration;

import com.askwinston.model.Product;
import com.askwinston.model.ProductQuantity;
import com.askwinston.repository.ProductQuantityRepository;
import com.askwinston.repository.ProductRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class ProductsMigrationService {

    private ProductRepository productRepository;
    private ProductQuantityRepository productQuantityRepository;

    private Set<String> productNamesSet = new HashSet<>();

    public ProductsMigrationService(ProductRepository productRepository, ProductQuantityRepository productQuantityRepository) {
        this.productRepository = productRepository;
        this.productQuantityRepository = productQuantityRepository;
        init();
        migrateProducts();
    }

    private void init() {
        productNamesSet.add("Sildenafil");
        productNamesSet.add("Tadalafil");
        productNamesSet.add("Minoxidil 5% Foam");
        productNamesSet.add("Finasteride");
        productNamesSet.add("Minoxidil 5% Foam and Finasteride Combo Pack");
        productNamesSet.add("Tretinoin 0.025%");
        productNamesSet.add("Clindamycin + Benzoyl Peroxide");
        productNamesSet.add("Valacyclovir");
        productNamesSet.add("Acyclovir Topical");
    }

    void updateOrSaveProduct(Product newProduct, List<ProductQuantity> quantities) {
        Product product = productRepository.findByName(newProduct.getName());
        boolean isNewProduct = false;
        if (product != null) {
            newProduct.setId(product.getId());
            newProduct.setQuantities(product.getQuantities());

        } else {
            newProduct = productRepository.save(newProduct);
            isNewProduct = true;
        }
        Product finalProduct = newProduct;
        if (!isNewProduct) {
            quantities.forEach(q -> {
                if (finalProduct.getQuantities().contains(q)) {
                    Optional<ProductQuantity> quantityOptional = finalProduct.getQuantities().stream()
                            .filter(oldQ -> oldQ.getQuantity().equals(q.getQuantity()))
                            .findFirst();
                    quantityOptional.ifPresent(quantity -> q.setId(quantity.getId()));
                }
            });
            finalProduct.getQuantities().stream()
                    .filter(q -> !quantities.contains(q))
                    .forEach(q -> productQuantityRepository.delete(q));
        }
        quantities.forEach(q -> {
            q.setProduct(finalProduct);
            q = productQuantityRepository.save(q);
            finalProduct.getQuantities().add(q);
        });
        productRepository.save(finalProduct);
    }


    void migrateProducts() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        Product product;
        ClassPathResource resource;
        String safetyInfo;
        List<ProductQuantity> quantities = new ArrayList<>();
        try {
            //Sildenafil
            resource = new ClassPathResource("products/safetyInfo/SildenafilSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Sildenafil")
                    .subName("Active ingredient in Viagra")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.ED)
                    .description("Intended to be taken right before sexual activity. It takes about 30-60 minutes to kick in and lasts 4-5 hours. It’s called getting lucky for a reason, it’s not always planned.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("dose")
                    .startingAtPrice("9.00")
                    .dosingTips("<ul><li>It's recommended that you take sildenafil on an empty stomach to ensure maximum effect and absorption.</li>" +
                            "<li>This medication should only be taken once a day.</li></ul>")
                    .productPageText("Whether planned or spontaneous this medication is great for a boost, taking effect within 1 hour and lasts 4-5 hours.  This might be the best option if your symptoms come and go.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "4 pack (100mg) - $12.00 per dose", 4800L, 1, "Sildenafil 4 pack of 100 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, product, "8 pack (100mg) - $10.50 per dose", 8400L, 2, "Sildenafil 8 pack of 100 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, product, "12 pack (100mg) - $9.00 per dose", 10800L, 3, "Sildenafil 12 pack of 100 mg", ProductQuantity.Supply.DEFAULT, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Tadalafil
            resource = new ClassPathResource("products/safetyInfo/TadalafilSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Tadalafil")
                    .subName("Active ingredient in Cialis")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.ED)
                    .description("Intended to be taken 2 hours before sexual activity but last up to 36 hours. This medication can be taken as a daily regimen or when the occasion arises.  For daily use, a lower dosage is recommended.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("dose")
                    .startingAtPrice("3.57")
                    .dosingTips("<ul><li>Try the low dose, once daily 5 mg pill.</li>" +
                            "<li>To use this product in the moment, a larger dose such as a 20mg tablet is recommended and should not be combined with a low daily dose.</li></ul>")
                    .productPageText("This medication can be used both daily or as a boost when needed.  But make sure you have the right dose for what you need.  A boost dose will be higher than a daily dose.  Our Doctors can help you understand what regimen is best for your symptoms during your consultation.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "4 pack (20mg) - $13.50 per dose", 5400L, 1, "Tadalafil 4 pack of 20 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, null, "8 pack (20mg) - $12.00 per dose", 9600L, 2, "Tadalafil 8 pack of 20 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, null, "12 pack (20mg) - $11.00 per dose", 13200L, 3, "Tadalafil 12 pack of 20 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, null, "28 daily pack (5mg) - $3.57 per dose", 10000L, 4, "Tadalafil 28 pack of 5 mg", ProductQuantity.Supply.DEFAULT, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Minoxidil 5% Foam
            resource = new ClassPathResource("products/safetyInfo/MinoxidilSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Minoxidil 5% Foam")
                    .subName("Active ingredient in Rogaine")
                    .category(Product.Category.FOAM)
                    .problemCategory(Product.ProblemCategory.HL)
                    .description("This is a topical treatment that revives hair follicles that have shrunk due to hereditary hair loss. When used consistently this can help you grow thicker, fuller hair. It is most effective for thinning at the crown or top of the head")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("40.00")
                    .dosingTips("<ul><li>Apply on the top of the head twice daily</li>" +
                            "<li>To achieve the best results, it is recommended to use this treatment for at least 16 weeks.</li></ul>")
                    .productPageText("This is your go-to for thinning hair on the top of your head or crown.  Used twice daily it can help give you hair she’ll want to run her fingers through.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $50.00 per month", 5000L, 1, "Minoxidil 5% 1 x 60 g", ProductQuantity.Supply.PERIOD_30, false, 0));
            quantities.add(new ProductQuantity(null, null, "3 month supply - $40.00 per month", 12000L, 2, "Minoxidil 5% 3 x 60 g", ProductQuantity.Supply.PERIOD_90, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Finasteride
            resource = new ClassPathResource("products/safetyInfo/FinasterideSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Finasteride")
                    .subName("Active ingredient in Propecia")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.HL)
                    .description("This medication treats hair loss by blocking DHT (dihydrotestosterone). DHT is a hormone derived from testosterone and is considered to be the main cause of male hair loss. It is used to stop all hair loss on top of the head, including receding hairlines.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("40.00")
                    .dosingTips("<ul><li>Take 1mg orally once a day.</li>" +
                            "<li>To achieve the best results, it is recommended to use this treatment for at least 1 year.</li></ul>")
                    .productPageText("By attacking the hormone DHT, this medication can help you stop hair loss in its tracks. So, you can keep your hair and lose the hats.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $45.00 per month", 4500L, 1, "Finasteride 30 pills of 1 mg", ProductQuantity.Supply.PERIOD_30, false, 0));
            quantities.add(new ProductQuantity(null, null, "3 month supply - $40.00 per month", 12000L, 2, "Finasteride 90 pills of 1 mg", ProductQuantity.Supply.PERIOD_90, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Tretinoin 0.025%
            resource = new ClassPathResource("products/safetyInfo/TretinoinSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Tretinoin 0.025%")
                    .subName("Active ingredient in Retin-A")
                    .category(Product.Category.CREAM)
                    .problemCategory(Product.ProblemCategory.AC)
                    .description("A derivative of vitamin A, this topical is used on the skin to treat mild to moderate acne. It works by increasing skin cell turnover, which promotes the expulsion of the plugged material in the follicle. In patients with acne, new cells replace the cells of existing pimples, and the rapid turnover of cells prevents new pimples from forming. Tretinoin can also help reduce fine wrinkles and areas of darkened skin.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("30.00")
                    .dosingTips("<ul><li>Prior to application of topical tretinoin, cleanse the affected area with alcohol free soap and let it dry. Wait 20 minutes before applying the treatment. When applying the topical, gently rub it into the affected area.</li>" +
                            "<li>Apply once daily, in the evening.</li></ul>")
                    .productPageText("Getting your vitamins takes on a whole new meaning!  Derived from Vitamin A, you can treat your mild to moderate acne with this daily topical treatment.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $30.00 per month", 3000L, 1, "Retin-A Micro Tretinoin Gel 0.025% 30 g", ProductQuantity.Supply.PERIOD_30, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Valacyclovir
            resource = new ClassPathResource("products/safetyInfo/ValacyclovirSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Valacyclovir")
                    .subName("Active ingredient in Valtrex")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.CS)
                    .description("This antiviral medication is used to treat oral herpes, which are caused by herpes simplex virus type 1 (HSV-1) and herpes simplex virus type 2 (HSV-2). Valacyclovir decreases the severity and length of outbreaks allowing the sores to heal faster, keeps new sores from forming, and decreases pain/itching.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("outbreak")
                    .startingAtPrice("20.00")
                    .dosingTips("<ul><li>Start taking this medication at the first sign or as soon as you feel tingling, itching, burning or start noticing a blister forming. Take 2g of Valacyclovir 12 hours apart for 1 day at the first sign of an outbreak (single episode).</li>" +
                            "<li>This medication can be used both for breakouts (as long as you start it as soon as you notice symptoms) or as a daily therapy for those who have frequent breakouts (9 or more a year) to treat oral herpes and keep breakouts from happening. Daily therapy also reduces the chances of spreading the virus to others.  Take 500 mg once daily if experiencing 9 or more outbreaks in a year.</li></ul>")
                    .productPageText("Never worry about going in for a lip lock again with this antiviral treatment.  By helping cold sores to heal faster, decrease symptoms and stopping new cold sores form appearing your lips will thank you.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 outbreak - $20.00 per outbreak", 2000L, 1, "Valacyclovir 8 pills of 500 mg", ProductQuantity.Supply.OUTBREAK_1, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Clindamycin + Benzoyl Peroxide
            resource = new ClassPathResource("products/safetyInfo/ClindamycinBenzoylPeroxideSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Clindamycin + Benzoyl Peroxide")
                    .subName("Active ingredients in Clindoxyl")
                    .category(Product.Category.CREAM)
                    .problemCategory(Product.ProblemCategory.AC)
                    .description("This topical gel is a combination topical treatment with an antibiotic (clindamycin) and benzoyl peroxide, which is a peeling agent and can help treat mild to moderate acne.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("60.00")
                    .dosingTips("<ul><li>Before bedtime, apply to the affected skin after the skin has been washed, rinsed with warm water, and gently patted dry.</li>" +
                            "<li>Avoid contact with eyes, nostrils, mouth, and sensitive skin. If contact does occur, rinse with plenty of cool tap water.</li>" +
                            "<li>Wash your hands before and after using this medication.</li></ul>")
                    .productPageText("Peel and protect with this combo treatment.  This treatment is a great nighttime routine to fight mild to moderate acne.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $60.00 per month", 6000L, 1, "Clindamycin/Benzoyl Peroxide Gel (5%/1% W/W) 45 g", ProductQuantity.Supply.PERIOD_30, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Acyclovir Topical
            resource = new ClassPathResource("products/safetyInfo/AcyclovirSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Acyclovir Topical")
                    .subName("Active ingredient in Zovirax")
                    .category(Product.Category.CREAM)
                    .problemCategory(Product.ProblemCategory.CS)
                    .description("This antiviral slows the growth and spread of the herpes virus so that the body can fight off the infection. Acyclovir topical cream is used to treat cold sores around the mouth and face. This medication can decrease the length of time you have pain and help the sores heal faster. It may be a great option in case you are unable to take the pills or just prefer a topical treatment.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("outbreak")
                    .startingAtPrice("70.00")
                    .dosingTips("<ul><li>Typically applied 5 times a day to the affected area for 4 days. You should start using acyclovir ointment as soon as possible after you experience the first symptoms of infection.</li>" +
                            "<li>Acyclovir cream may be applied at any time during a cold sore outbreak, but it works best if applied at the very beginning when there is tingling, redness, itching, or a bump but the cold sore has not yet formed.</li></ul>")
                    .productPageText("Kick cold sores to the curb faster with this daily topical. If you aren’t a fan of pills or a more regular regimen, this spot treatment could be the best option for you.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 outbreak - $70.00 per outbreak", 7000L, 1, "Acyclovir 5% 4 g", ProductQuantity.Supply.OUTBREAK_1, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Minoxidil 5% Foam and Finasteride
            resource = new ClassPathResource("products/safetyInfo/MinoxidilSafetyInfo.html");
            String safetyInfo1 = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            resource = new ClassPathResource("products/safetyInfo/FinasterideSafetyInfo.html");
            String safetyInfo2 = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            safetyInfo = "<h2>Minoxidil 5% Foam</h2>\n" + safetyInfo1 + "\n<h2>Finasteride</h2>\n" + safetyInfo2;
            product = Product.builder()
                    .name("Minoxidil 5% Foam and Finasteride")
                    .subName("")
                    .category(Product.Category.COMBO_PACK)
                    .problemCategory(Product.ProblemCategory.HL)
                    .description("Many patients see the best results when using both of these treatments in combination and help with hair loss on the entire head. Get the combination pack at a lower cost than buying these two products separately.")
                    .safetyInfo(safetyInfo)
                    .isCombo(true)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("60.00")
                    .dosingTips("<ul><li>Fuel hair growth AND prevent further hair loss at the same time by utilizing both products at once.</li>" +
                            "<li>Take Finasteride 1mg orally once a day and apply Minoxidil 5% foam on the crown and top of the head twice daily.</li></ul>")
                    .productPageText("Give your hair loss the 1-2 punch!  Talk with one of our Doctors today and find out if a combination pack is the best treatment choice for you.")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $70.00 per month", 7000L, 1, "Finasteride 30 pills of 1 mg + Minoxidil 5% 1 x 60 g", ProductQuantity.Supply.PERIOD_30, false, 0));
            quantities.add(new ProductQuantity(null, null, "3 month supply - $60.00 per month", 18000L, 2, "Finasteride 90 pills of 1 mg + Minoxidil 5% 3 x 60 g", ProductQuantity.Supply.PERIOD_90, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
