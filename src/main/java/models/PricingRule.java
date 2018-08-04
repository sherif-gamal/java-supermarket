package models;

import java.util.Map;

/**
 * Created by sherif on 4/8/18.
 */
public abstract class PricingRule {
    protected Catalogue catalogue;

    public abstract boolean applies(Map<String, Integer> basket);

    public abstract void exec(Map<String, Integer> initialBasket, Map<Product, Integer> finalBasket) throws RuleDoesNotApplyException;
}
