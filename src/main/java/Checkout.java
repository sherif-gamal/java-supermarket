import models.Catalogue;
import models.PricingRule;
import models.Product;
import models.RuleDoesNotApplyException;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by sherif on 4/8/18.
 */
public class Checkout {

    private Map<String, Integer> basket;
    private List<PricingRule> pricingRules;
    private Catalogue catalogue;

    public Checkout(List<PricingRule> pricingRules) {
        this.pricingRules = pricingRules;
        this.basket = new HashMap<>();
    }

    public void setCatalogue(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

    public void scan(String sku) {
        Integer alreadyAdded = basket.get(sku);
        basket.put(sku, alreadyAdded == null ? 1 : alreadyAdded + 1);
    }

    public double total() {
        Map<Product, Integer> finalBasket = new HashMap<>();
        pricingRules.forEach(pricingRule -> {
            if (pricingRule.applies(basket)) {
                try {
                    pricingRule.exec(basket, finalBasket);
                } catch (RuleDoesNotApplyException e) {
                    e.printStackTrace(); // not the best handling, should probably do more if in production
                }
            }
        });

        basket.entrySet()
                .forEach(e -> finalBasket.put(catalogue.findProduct(e.getKey()), e.getValue()));

        return finalBasket.entrySet()
                .stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }
}
