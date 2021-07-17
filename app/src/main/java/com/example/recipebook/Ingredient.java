package com.example.recipebook;

public class Ingredient {
    String amount;
    String unit;
    String product;
    String forPart;//optional

    public Ingredient() {
    }

    public String getForPart() {
        return forPart;
    }

    public void setForPart(String forPart) {
        this.forPart = forPart;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }




}
