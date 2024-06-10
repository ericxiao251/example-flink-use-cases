package org.simpleWebServer;

public class Coffee {

    public enum CoffeeSize {
        S, M, L, XL;
    }

    private CoffeeSize size;
    private String name;
    private long prepTime;

    public CoffeeSize getSize() {
        return this.size;
    }

    public String getName() {
        return this.name;
    }

    public long getPrepTime() {
        return this.prepTime;
    }

    public void setSize(CoffeeSize size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrepTime(long prepTime) {
        this.prepTime = prepTime;
    }
}
