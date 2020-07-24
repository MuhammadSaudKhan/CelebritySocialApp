package com.saud.celebrityapp.Model;

public class FileModel {
    private String id;
    private String name;
    private String url;
    private String price;

    public FileModel() {
    }

    public FileModel(String id, String name, String url, String price) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
