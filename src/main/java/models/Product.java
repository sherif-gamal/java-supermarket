package models;

/**
 * Created by sherif on 4/8/18.
 */
public class Product implements Cloneable {
    private String sku;
    private String name;
    private double price;

    public Product(String sku, String name, double price) {
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public Product clone() {
        return new Product(this.sku, this.name, this.price);
    }
}
