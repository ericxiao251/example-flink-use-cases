package org.simpleWebServer;

public class Coffee {
    private String size;
    private String name;
    private long prepTime;

    public String getSize() {
        return this.size;
    }

    public String getName() {
        return this.name;
    }

    public long getPrepTime() {
        return this.prepTime;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrepTime(long prepTime) {
        this.prepTime = prepTime;
    }
}
