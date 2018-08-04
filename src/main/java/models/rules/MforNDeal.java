package models.rules;

import models.Catalogue;
import models.PricingRule;
import models.Product;
import models.RuleDoesNotApplyException;

import java.util.Map;

/**
 * Created by sherif on 4/8/18.
 * Get M for the value of N
 */
public class MforNDeal extends PricingRule {
    String sku;
    int m, n;

    /**
     * @param catalogue the catalogue
     * @param sku the product sku
     * @param m the number of items bought
     * @param n the number of items paid for
     */
    public MforNDeal(Catalogue catalogue, String sku, int m, int n) {
        this.catalogue = catalogue;
        this.sku = sku;
        this.m = m;
        this.n = n;
    }

    public boolean applies(Map<String, Integer> basket) {
        Integer num = basket.get(sku);
        return num != null && num >= m;
    }

    @Override
    public void exec(Map<String, Integer> initialBasket, Map<Product, Integer> finalBasket) throws RuleDoesNotApplyException {
        Product product = catalogue.findProduct(sku);
        Integer num = initialBasket.get(sku);
        if (num < m) {
            throw new RuleDoesNotApplyException();
        }
        // If n = 2 and m = 3 and the user buys 8 items

        // the price will be (price * 2 / 3)
        Product modified = product.clone();
        modified.setPrice(product.getPrice() * n / m);

        /*
        (8 / 3 * 3) = 6 items for the discounted price
        (8 % 3) = 2 items for the original price
         */
        finalBasket.put(modified, (num / m) * m);
        if (num > m) {
            finalBasket.put(product, num % m);
        }
        initialBasket.remove(sku);
    }
}
