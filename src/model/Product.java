package model;

import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private float price;
    private ProductType productType;
    private int stockQuantity;
    private String description;

    public Product(){

    }

    public Product(int id, String name, float price, ProductType productType, int stockQuantity, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(id>0){
            this.id = id;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null && name.length()>=3){
            this.name = name;
        }
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        if(price>0.0){
            this.price = price;
        }
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        if (productType != null){
            this.productType = productType;
        }
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        if(stockQuantity>=0){
            this.stockQuantity = stockQuantity;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description != null && description.length()>3){
            description = description;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", productType=" + productType +
                ", stockQuantity=" + stockQuantity +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Float.compare(price, product.price) == 0 && stockQuantity == product.stockQuantity && Objects.equals(name, product.name) && productType == product.productType && Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, productType, stockQuantity, description);
    }
}
