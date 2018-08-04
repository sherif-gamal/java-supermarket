package models.rules;

import models.Catalogue;
import models.PricingRule;
import models.Product;
import models.RuleDoesNotApplyException;

import java.util.Map;

/**
 * Created by sherif on 4/8/18.
 */
public class YFreeForX extends PricingRule {
    String skuX, skuY;

    public YFreeForX(Catalogue catalogue, String skuX, String skuY) {
        this.catalogue = catalogue;
        this.skuX = skuX;
        this.skuY = skuY;
    }

    public boolean applies(Map<String, Integer> basket) {
        Integer num = basket.get(skuX);
        return num != null && num >= 1;
    }
    @Override
    public void exec(Map<String, Integer> initialBasket, Map<Product, Integer> finalBasket) throws RuleDoesNotApplyException {
        Product productX = catalogue.findProduct(skuX);
        Integer numX = initialBasket.get(skuX);
        if (numX < 0) {
            throw new RuleDoesNotApplyException();
        }

        finalBasket.put(productX, numX);

        Product productY = catalogue.findProduct(skuY);
        Product modified = productY.clone();
        modified.setPrice(0);
        finalBasket.put(modified, numX);

        // don't include the free Y items in any other rules
        Integer numY = initialBasket.get(skuY);
        if (numY > numX)
            initialBasket.put(skuY, numY - numX);
        else
            initialBasket.remove(skuY);
    }
}
