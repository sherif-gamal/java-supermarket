import models.Catalogue;
import models.PricingRule;
import models.Product;
import models.rules.BulkDiscountDeal;
import models.rules.MforNDeal;
import models.rules.YFreeForX;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sherif on 4/8/18.
 */
public class CheckoutTest {

    private static List<PricingRule> createPricingRules(Catalogue catalogue) {
        List<PricingRule> rules = new ArrayList<>();
        rules.add(new MforNDeal(catalogue, "atv", 3, 2));
        rules.add(new BulkDiscountDeal(catalogue, "ipd", 4, 499.99));
        rules.add(new YFreeForX(catalogue, "mbp", "vga"));
        return rules;
    }

    private Catalogue buildCatalogue() {
        Catalogue catalogue =  new Catalogue();
        catalogue.addProduct(new Product("ipd",	"Super iPad",	549.99));
        catalogue.addProduct(new Product("mbp",	"MacBook Pro",	1399.99));
        catalogue.addProduct(new Product("atv",	"Apple TV",	109.50));
        catalogue.addProduct(new Product("vga",	"VGA adapter",	30.00));
        return catalogue;
    }

    @Test
    public void testMForNApplies() {
        Catalogue catalogue = buildCatalogue();

        PricingRule pricingRule = new MforNDeal(catalogue, "atv", 3, 2);
        Map<String, Integer> basket = new HashMap<>();

        Assert.assertFalse(pricingRule.applies(basket)); // 0 items should => doesn't apply
        basket.put("atv", 2);
        Assert.assertFalse(pricingRule.applies(basket)); // 2 items => doesn't apply
        basket.put("atv", 3);
        Assert.assertTrue(pricingRule.applies(basket)); // 3 items => applies
        basket.put("atv", 8);
        Assert.assertTrue(pricingRule.applies(basket)); // more than 8 items => applies
    }

    @Test
    public void testYFreeForXApplies() {
        Catalogue catalogue = buildCatalogue();

        PricingRule pricingRule = new YFreeForX(catalogue, "mbp", "vga");
        Map<String, Integer> basket = new HashMap<>();

        Assert.assertFalse(pricingRule.applies(basket)); // 0 items should => doesn't apply
        basket.put("mbp", 1);
        Assert.assertTrue(pricingRule.applies(basket)); // 1 item => applies
        basket.put("mbp", 3);
        Assert.assertTrue(pricingRule.applies(basket)); // more than 1 item => applies
    }

    @Test
    public void testBulkDiscountApplies() {
        Catalogue catalogue = buildCatalogue();

        PricingRule pricingRule = new BulkDiscountDeal(catalogue, "ipd", 4, 444);
        Map<String, Integer> basket = new HashMap<>();

        Assert.assertFalse(pricingRule.applies(basket)); // 0 items should => doesn't apply
        basket.put("ipd", 4);
        Assert.assertTrue(pricingRule.applies(basket)); // 4 item => applies
        basket.put("ipd", 18);
        Assert.assertTrue(pricingRule.applies(basket)); // more than 4 items => applies
    }

    @Test
    public void testMforN() {
       Catalogue catalogue = buildCatalogue();
       List<PricingRule> pricingRules = createPricingRules(catalogue);

       Checkout checkout = new Checkout(pricingRules);
       checkout.setCatalogue(catalogue);

        String[] skus = new String[]{"atv", "atv", "atv", "vga"};
        for (String sku: skus) {
            checkout.scan(sku);
        }
        Assert.assertEquals(249.00, checkout.total(), 0);
    }

    @Test
    public void testBulkDiscounted() {
        Catalogue catalogue = buildCatalogue();
        List<PricingRule> pricingRules = createPricingRules(catalogue);
        Checkout checkout = new Checkout(pricingRules);
        checkout.setCatalogue(catalogue);

        String[] skus = new String[]{"atv", "ipd", "ipd", "atv", "ipd", "ipd", "ipd"};
        for (String sku: skus) {
            checkout.scan(sku);
        }
        Assert.assertEquals(2718.95, checkout.total(), 0);
    }

    @Test
    public void testYFreeForX() {
        Catalogue catalogue = buildCatalogue();
        List<PricingRule> pricingRules = createPricingRules(catalogue);
        Checkout checkout = new Checkout(pricingRules);
        checkout.setCatalogue(catalogue);

        String[] skus = new String[]{"mbp", "vga", "ipd"};
        for (String sku: skus) {
            checkout.scan(sku);
        }
        Assert.assertEquals(1949.98, checkout.total(), 0);
    }

    @Test
    public void testNonExistentProduct() {
        Catalogue catalogue = buildCatalogue();
        List<PricingRule> pricingRules = createPricingRules(catalogue);
        Checkout checkout = new Checkout(pricingRules);
        checkout.setCatalogue(catalogue);

        String[] skus = new String[]{"mbp", "vga", "xyz", "ipd"};
        for (String sku: skus) {
            checkout.scan(sku);
        }
        Assert.assertEquals(1949.98, checkout.total(), 0);
    }

    @Test
    public void testAll() {
        Catalogue catalogue = buildCatalogue();
        List<PricingRule> pricingRules = createPricingRules(catalogue);
        Checkout checkout = new Checkout(pricingRules);
        checkout.setCatalogue(catalogue);

        /*
        3 atv * 73.00
        2 atv * 109.50
        1 mbp * 1399.99
        1 vga * 0
        1 vga * 30
        6 ipd * 499.99
         */
        String[] skus = new String[]{"atv", "atv", "atv", "atv", "atv",
                "mbp", "vga", "vga",
                "ipd", "ipd", "ipd", "ipd", "ipd", "ipd"
        };
        for (String sku: skus) {
            checkout.scan(sku);
        }
        Assert.assertEquals(4867.93, checkout.total(), 0);
    }
}
