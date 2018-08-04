package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sherif on 4/8/18.
 */
public class Catalogue {
    private Map<String, Product> products;

    public Catalogue() {
        this.products = new HashMap<>();
    }

    public void addProduct(Product product) {
        products.put(product.getSku(), product);
    }

    public Product findProduct(String sku) {
        return products.get(sku);
    }
}
