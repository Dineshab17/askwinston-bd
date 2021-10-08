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
        productNamesSet.add("Rupall");
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
                    .subName("Ingredient in Viagra")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.ED)
                    .description("Intended to be taken right before sexual activity. It takes about 30-60 minutes to kick in and lasts 4-5 hours. It’s called getting lucky for a reason, it’s not always planned.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("dose")
                    .startingAtPrice("48.00")
                    .dosingTips("<ul><li>It's recommended that you take sildenafil on an empty stomach to ensure maximum effect and absorption.</li>" +
                            "<li>This medication should only be taken once a day.</li></ul>")
                    .productPageText("<p>Whether planned or spontaneous this medication is great for a boost, taking effect within 1 hour and lasts 4-5 hours. This might be the best option if your symptoms come and go.</p>")
                    .ingredient("<p>Nitroglycerin<br>Isosorbide dinitrate<br>Isosorbide mononitrate<br>Amyl nitrate<br>Amyl nitrite<br>Butyl nitrate</p>")
                    .landingPageText("<p>Sildenafil is the active compound in the drug popularly known as Viagra. It treats erectile dysfunction by increasing blood flow to the penis to help you get an erection faster.<br><br>\u200DIt is best to take this medication right before sexual activity. It takes about 30-60 minutes to kick in and lasts 4-5 hours. It’s called getting lucky for a reason, it’s not always planned.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "4 pack (100mg) - $48.00", 4800L, 1, "Sildenafil 4 pack of 100 mg", ProductQuantity.Supply.DEFAULT, false, 1));
            quantities.add(new ProductQuantity(null, product, "8 pack (100mg) - $84.00", 8400L, 2, "Sildenafil 8 pack of 100 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, product, "12 pack (100mg) - $108.00", 10800L, 3, "Sildenafil 12 pack of 100 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Tadalafil
            resource = new ClassPathResource("products/safetyInfo/TadalafilSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Tadalafil")
                    .subName("Ingredient in Cialis")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.ED)
                    .description("Intended to be taken 2 hours before sexual activity but last up to 36 hours. This medication can be taken as a daily regimen or when the occasion arises.  For daily use, a lower dosage is recommended.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("dose")
                    .startingAtPrice("54.00")
                    .dosingTips("<ul><li>If you are prescribed the low dose, one 5 mg pill daily is suitable.</li>" +
                            "<li>To use this product in the moment, a larger dose such as a 20mg tablet may be recommended and should not be combined with a low daily dose.</li>" +
                            "<li>Tadalafil can be taken with or without food.</li></ul>")
                    .productPageText("<p>This medication is great for both planned and spontaneous sexual activities. Intended to be taken 2 hours before sexual activity, Tadalafil can last up to 36 hours.<br><br>This medication can be taken as a daily regimen or when the occasion arises. For daily use, a lower dosage is recommended. Our doctors can help you understand what regimen is best for your symptoms during your consultation.</p>")
                    .ingredient("<p>Nitroglycerin<br>Isosorbide dinitrate<br>Isosorbide mononitrate<br>Amyl nitrate<br>Amyl nitrite<br>Butyl nitrate</p>")
                    .landingPageText("<p>Tadalafil is the active compound in the drug popularly known as Cialis. It treats erectile dysfunction by increasing blood flow to the penis to help you get an erection faster.<br><br>This medication is intended to be taken 2 hours before sexual activity with its effects lasting up to 36 hours. This medication can be taken as a daily regimen or when the occasion arises. For daily use, a lower dosage is recommended.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "4 pack (20mg) - $54.00", 5400L, 1, "Tadalafil 4 pack of 20 mg", ProductQuantity.Supply.DEFAULT, false, 1));
            quantities.add(new ProductQuantity(null, null, "8 pack (20mg) - $96.00", 9600L, 2, "Tadalafil 8 pack of 20 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, null, "12 pack (20mg) - $132.00", 13200L, 3, "Tadalafil 12 pack of 20 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            quantities.add(new ProductQuantity(null, null, "28 daily pack (5mg) - $100.00", 10000L, 4, "Tadalafil 28 pack of 5 mg", ProductQuantity.Supply.DEFAULT, false, 0));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Minoxidil 5% Foam
            resource = new ClassPathResource("products/safetyInfo/MinoxidilSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Minoxidil 5% Foam")
                    .subName("Ingredient in Rogaine")
                    .category(Product.Category.FOAM)
                    .problemCategory(Product.ProblemCategory.HL)
                    .description("This is a topical treatment that revives hair follicles that have shrunk due to hereditary hair loss. When used consistently this can help you grow thicker, fuller hair. It is most effective for thinning at the crown or top of the head")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("50.00")
                    .dosingTips("<ul><li>Apply on the top of the head twice daily</li>" +
                            "<li>To achieve the best results, it is recommended to use this treatment for at least 16 weeks.</li></ul>")
                    .productPageText("<p>Is your hair thinning at the top of your head? This may be the solution you need.<br><br> Minoxidil 5% Foam is a topical hair loss treatment that reverses baldness by helping you grow thicker and fuller hair.</p>")
                    .ingredient("<p>Minoxidil</p>")
                    .landingPageText("<p>Minoxidil 5% is the active component in Rogaine. Minoxidil 5% foam is a topical treatment used in the treatment of hereditary hair loss, also known as male pattern baldness.<br><br>This medication works by creating a favourable environment for your hair follicles to grow healthy, long hair. It does this by increasing the supply of blood and nutrients to your hair follicles. Minoxidil 5% foam also stimulates your hair follicles to go into the anagen, or growth phase, and maintain that growth for much longer.<br><br>If your type of hair loss is at the crown or top of the head, using this product consistently can help you grow thicker and longer hair</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $50.00", 5000L, 1, "Minoxidil 5% 1 x 60 g", ProductQuantity.Supply.PERIOD_30, false, 1));
            quantities.add(new ProductQuantity(null, null, "3 month supply - $120.00", 12000L, 2, "Minoxidil 5% 3 x 60 g", ProductQuantity.Supply.PERIOD_90, false, 0));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Finasteride
            resource = new ClassPathResource("products/safetyInfo/FinasterideSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Finasteride")
                    .subName("Ingredient in Propecia")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.HL)
                    .description("This medication treats hair loss by blocking DHT (dihydrotestosterone). DHT is a hormone derived from testosterone and is considered to be the main cause of male hair loss. It is used to stop all hair loss on top of the head, including receding hairlines.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("45.00")
                    .dosingTips("<ul><li>Take 1mg orally once a day.</li>" +
                            "<li>To achieve the best results, it is recommended to use this treatment for at least 1 year.</li>" +
                            "<li>Finasteride can be taken with or without food.</li></ul>")
                    .productPageText("<p>Deal with the root of your hair loss with Finasteride. This treatment works by blocking dihydrotestosterone (DHT), the hormone responsible for hair loss in men. With this hormone's level decreased, your hair can grow back at your balding spots.</p>")
                    .ingredient("<p>Finasteride</p>")
                    .landingPageText("<p>Finasteride is the active ingredient in Propecia used to treat all hair loss on the top of the head and receding hairlines in men.<br><br>Finasteride works by blocking DHT (Dihydrotestosterone), a hormone considered to be the cause of all male hair loss.<br><br>When there is a decreased level of DHT, it leads to an increase in hair growth on the head and slower hair loss. This leaves you with full and thick hair.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $45.00", 4500L, 1, "Finasteride 30 pills of 1 mg", ProductQuantity.Supply.PERIOD_30, false, 1));
            quantities.add(new ProductQuantity(null, null, "3 month supply - $120.00", 12000L, 2, "Finasteride 90 pills of 1 mg", ProductQuantity.Supply.PERIOD_90, false, 0));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Tretinoin 0.025%
            resource = new ClassPathResource("products/safetyInfo/TretinoinSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Tretinoin 0.025%")
                    .subName("Ingredient in Retin-A")
                    .category(Product.Category.CREAM)
                    .problemCategory(Product.ProblemCategory.AC)
                    .description("A derivative of vitamin A, this topical is used on the skin to treat mild to moderate acne. It works by increasing skin cell turnover, which promotes the expulsion of the plugged material in the follicle. In patients with acne, new cells replace the cells of existing pimples, and the rapid turnover of cells prevents new pimples from forming. Tretinoin can also help reduce fine wrinkles and areas of darkened skin.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("30.00")
                    .dosingTips("<ul><li>Before application of topical Tretinoin, cleanse the affected area with alcohol-free soap and let it dry. Wait 20 minutes before applying the treatment. When applying the topical cream or gel, gently rub it into the affected area.</li>" +
                            "<li>Apply once daily in the evening.</li></ul>")
                    .productPageText("<p>Skincare is healthcare. Get your skin back with Tretinoin 0.025% cream or gel, a safe and effective treatment for acne.<br><br>Tretinoin works by unclogging follicles and expelling trapped acne-causing substances from the skin. Tretinoin also stimulates rapid cell turnover, reducing the appearance of new pimples or acne scars</p>")
                    .ingredient("<p>Tretinoin<br/>Clindamycin (Clindamycin Phosphate)</p>")
                    .landingPageText("<p>Tretinoin is classified under the category of Vitamin A-derived medications called retinoids. Retinoids stimulate the generation of new skin cells and have been proven to effectively treat skin conditions like acne, psoriasis, and skin aging.<br><br>This topical medication, also known by its name Retin-A, works to treat acne by unblocking clogged follicles. The unclogged follicles release trapped bacteria and allow absorption of further treatment with antibiotics.<br><br>Tretinoin also helps your skin control its oil (sebum) production. In addition, because Tretinoin encourages new cell growth, it reduces the appearance of acne scars.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $30.00", 3000L, 1, "Retin-A Micro Tretinoin Gel 0.025% 30 g", ProductQuantity.Supply.PERIOD_30, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Valacyclovir
            resource = new ClassPathResource("products/safetyInfo/ValacyclovirSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Valacyclovir")
                    .subName("Ingredient in Valtrex")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.CS)
                    .description("This antiviral medication is used to treat oral herpes, which are caused by herpes simplex virus type 1 (HSV-1) and herpes simplex virus type 2 (HSV-2). Valacyclovir decreases the severity and length of outbreaks allowing the sores to heal faster, keeps new sores from forming, and decreases pain/itching.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("outbreak")
                    .startingAtPrice("20.00")
                    .dosingTips("<ul><li>Start taking this medication at the first sign or as soon as you feel tingling, itching, burning or start noticing a blister forming. Take 2g of Valacyclovir 12 hours apart for 1 day at the first sign of an outbreak (single episode).</li>" +
                            "<li>This medication can be used both for breakouts (if you start it as soon as you notice symptoms) or as a daily therapy for those who have frequent breakouts (9 or more a year) to treat oral herpes and keep breakouts from happening. Daily therapy also reduces the chances of spreading the virus to others.</li>" +
                            "<li>Take 500 mg once daily if experiencing 9 or more outbreaks in a year.</li>" +
                            "<li>Valacyclovir can be taken with or without food.</li>")
                    .productPageText("<p>Get your groove back. Whether you are newly infected or battling a recurring cold sore breakout, Valacyclovir could be the right treatment for you.<br><br> This antiviral medication works by inhibiting the spread and growth of the herpes virus and relieving your symptoms.</p>")
                    .ingredient("<p>Valacyclovir ( Valacyclovir Hydrochloride)</p>")
                    .landingPageText("<p>Valacyclovir is the active ingredient in Valtrex. It is an oral antiviral medication commonly used to treat conditions such as cold sores, genital herpes, and shingles caused by the herpes virus.<br><br>Valacyclovir works by inhibiting the growth and spread of the virus in the body, giving you enough time to fight the virus. By doing this, new sores will not form, and existing sores can heal faster, cause less pain, and itch less.<br><br>Valacyclovir doesn't prevent you from spreading the virus to others unless you are taking it on a daily basis, but it will lessen your symptoms and heal your sores.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 outbreak - $20.00", 2000L, 1, "Valacyclovir 8 pills of 500 mg", ProductQuantity.Supply.OUTBREAK_1, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Clindamycin + Benzoyl Peroxide
            resource = new ClassPathResource("products/safetyInfo/ClindamycinBenzoylPeroxideSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Clindamycin + Benzoyl Peroxide")
                    .subName("Ingredients in Clindoxyl")
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
                    .productPageText("<p>This topical gel is a combination topical treatment with an antibiotic (clindamycin) and benzoyl peroxide.<br><br>Clindamycin is an antibiotic that works by slowing down the growth of bacteria while Benzoyl Peroxide also helps kill bacteria that causes acne as well as reduces excess oil on the skin. As a combination, this topical gel is effective in treating mild to moderate acne.</p>")
                    .ingredient("<p>Benzoyl Peroxide</p>")
                    .landingPageText("<p>The Clindamycin and Benzoyl Peroxide combination are the active ingredients in Clindoxyl. They belong to a category of medications called topical antibiotics used to treat moderate acne.<br><br>Together, they help treat acne by fighting bacteria trapped in skin follicles that cause acne. Benzoyl Peroxide ensures that the bacteria does not develop resistance to medication.<br><br>This combination also reduces excess oil in the follicles, keeping your pores open.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $60.00", 6000L, 1, "Clindamycin/Benzoyl Peroxide Gel (5%/1% W/W) 45 g", ProductQuantity.Supply.PERIOD_30, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Acyclovir Topical
            resource = new ClassPathResource("products/safetyInfo/AcyclovirSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Acyclovir Topical")
                    .subName("Ingredient in Zovirax")
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
                    .productPageText("<p>Get your groove back, and kiss cold sores goodbye.<br><br> Acyclovir is an antiviral that has been proven to be effective in treating the virus that causes cold sores - Herpes. It works by stopping the growth of the virus. Acyclovir reduces the duration of cold sores and relieves infection symptoms.<br><br> Acyclovir topical cream is a great choice if you prefer topical treatment over oral medication valacyclovir.</p>")
                    .ingredient("<p>Acyclovir</p>")
                    .landingPageText("<p>Acyclovir is the active ingredient in Zovirax used in treating herpes-related infections. This medication works by slowing the growth and spread of the herpes virus and prevents new sores from forming.<br><br>This antiviral topical cream effectively treats cold sores around the mouth and face and will reduce symptoms of the infection. It can be used during any stage of the breakout but works better when applied in the initial stages before the sore forms.<br><br>Acyclovir topical cream is a great choice if you prefer topical treatment over oral medication. Acyclovir topical doesn't prevent you from spreading the virus to others, but it will reduce your symptoms and heal your sores.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 outbreak - $70.00", 7000L, 1, "Acyclovir 5% 4 g", ProductQuantity.Supply.OUTBREAK_1, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

            //Minoxidil 5% Foam and Finasteride
            resource = new ClassPathResource("products/safetyInfo/MinoxidilSafetyInfo.html");
            String safetyInfo1 = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            resource = new ClassPathResource("products/safetyInfo/FinasterideSafetyInfo.html");
            String safetyInfo2 = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            safetyInfo = "<h2 class='product-head'>Minoxidil 5% Foam</h2>\n" + safetyInfo1 + "\n<h2 class='product-head'>Finasteride</h2>\n" + safetyInfo2;
            product = Product.builder()
                    .name("Minoxidil 5% Foam and Finasteride Combo")
                    .subName("Combo Pack")
                    .category(Product.Category.COMBO_PACK)
                    .problemCategory(Product.ProblemCategory.HL)
                    .description("Many patients see the best results when using both of these treatments in combination and help with hair loss on the entire head. Get the combination pack at a lower cost than buying these two products separately.")
                    .safetyInfo(safetyInfo)
                    .isCombo(true)
                    .quantities(new ArrayList<>())
                    .startingAtForm("month")
                    .startingAtPrice("70.00")
                    .dosingTips("<ul><li>Fuel hair growth AND prevent further hair loss at the same time by utilizing both products at once.</li>" +
                            "<li>Take Finasteride 1mg orally once a day and apply Minoxidil 5% foam on the crown and top of the head twice daily.</li></ul>")
                    .productPageText("<p>Combat hair loss with a double punch! This combo pack provides proven results in treating hair loss in men.<br><br> Finasteride blocks DHT (dihydrotestosterone), which is the hormone responsible for hair loss in men, while Minoxidil 5% Foam creates a favourable environment for your hair follicles to grow long and thick healthy hair.</p>")
                    .ingredient("<p>Finasteride<br>Minoxidil</p>")
                    .landingPageText("<p>Many men have recorded outstanding results by combining both Minoxidil 5% foam and Finasteride for their hair loss treatment.<br><br>Finasteride blocks DHT (dihydrotestosterone), which is the hormone responsible for hair loss in men. At the same time, Minoxidil 5% foam creates a favourable environment for your hair follicles to grow long and thick healthy hair.<br><br>By combining both treatments, you get the best of both worlds.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "1 month supply - $70.00", 7000L, 1, "Finasteride 30 pills of 1 mg + Minoxidil 5% 1 x 60 g", ProductQuantity.Supply.PERIOD_30, false, 0));
            quantities.add(new ProductQuantity(null, null, "3 month supply - $180.00", 18000L, 2, "Finasteride 90 pills of 1 mg + Minoxidil 5% 3 x 60 g", ProductQuantity.Supply.PERIOD_90, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();

//            Rupall
            resource = new ClassPathResource("products/safetyInfo/RupallSafetyInfo.html");
            safetyInfo = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            product = Product.builder()
                    .name("Rupall")
                    .subName("Active Ingredient is Rupatadine")
                    .category(Product.Category.PILLS)
                    .problemCategory(Product.ProblemCategory.AL)
                    .description("Rupall works by inhibiting the action of one of the body's natural chemicals known as histamine. Histamine is responsible for many of the symptoms caused by allergies. Rupall can be safely taken all year round or as needed depending on the allergens and frequency of symptoms experienced.")
                    .safetyInfo(safetyInfo)
                    .isCombo(false)
                    .quantities(new ArrayList<>())
                    .startingAtForm("dose")
                    .startingAtPrice("40.00")
                    .dosingTips("<ul><li>Take this medication by mouth with or without food </li>" +
                            "<li>Use this medication regularly to get the most benefit from it</li>" +
                            "<li>Avoid eating grapefruit or drinking grapefruit juice while using this medication</li></ul>")
                    .productPageText("<p>As soon as the seasons change, many people start to dread the often unavoidable symptoms of allergies that accompany the season. Rupall may be the right medication for you to help manage and treat symptoms associated with allergic rhinitis.</p>")
                    .ingredient("<p>Rupatadine fumarate</p><p>Nonmedicinal ingredients: lactose monohydrate, magnesium stearate, microcrystalline cellulose, pregelatinised maize starch, red iron oxide, and yellow iron oxide.</p>")
                    .landingPageText("<p>Rupall (Rupatadine) is an oral medication which belongs to the class of medications called second-generation antihistamines. Rupall helps to manage and treat symptoms associated with seasonal and year-round allergies, and other skin conditions/disorders including chronic hives and itching. Symptoms can include sneezing, runny nose, itching, and tearing and redness of the eyes.</p>" +
                            "<p>Rupall works by inhibiting the action of one of the body's natural chemicals known as histamine. Histamine is responsible for many of the symptoms caused by allergies.</p>" +
                            "<p>Rupall can be safely taken all year round or as needed depending on the allergens and frequency of symptoms experienced.</p>")
                    .build();
            quantities.add(new ProductQuantity(null, null, "Rupatadine 10 mg – 30 pills - $40", 4000L, 1, "Rupatadine 10 mg – 30 pills", ProductQuantity.Supply.PERIOD_30, false, 1));
            quantities.add(new ProductQuantity(null, null, "Rupatadine 10 mg – 90 pills - $100", 10000L, 1, "Rupatadine 10 mg – 90 pills", ProductQuantity.Supply.PERIOD_90, false, 1));
            updateOrSaveProduct(product, quantities);
            quantities.clear();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
