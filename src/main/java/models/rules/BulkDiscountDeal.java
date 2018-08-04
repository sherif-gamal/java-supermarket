package models.rules;

import models.Catalogue;
import models.PricingRule;
import models.Product;
import models.RuleDoesNotApplyException;

import java.util.Map;

/**
 * Created by sherif on 4/8/18.
 */
public class BulkDiscountDeal extends PricingRule {
    int m;
    String sku;
    double newPrice;

    public BulkDiscountDeal(Catalogue catalogue, String sku, int m, double newPrice) {
        this.catalogue = catalogue;
        this.sku = sku;
        this.m = m;
        this.newPrice = newPrice;
    }

    @Override
    public boolean applies(Map<String, Integer> basket) {
        Integer num = basket.get(sku);
        return num != null && num >= m;
    }

    @Override
    public void exec(Map<String, Integer> initialBasket, Map<Product, Integer> finalBasket) throws RuleDoesNotApplyException {
        Product product = catalogue.findProduct(sku);
        Integer num = initialBasket.get(sku);
        if (num == null || num <= m) {
            throw new RuleDoesNotApplyException();
        }

        Product modified = product.clone();
        modified.setPrice(newPrice);
        finalBasket.put(modified, num);
        initialBasket.remove(sku);
    }
}
