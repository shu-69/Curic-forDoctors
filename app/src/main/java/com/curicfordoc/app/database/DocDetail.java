package com.curicfordoc.app.database;

public class DocDetail {
    private String id;
    private String name;
    private String specialization;
    private String experience;
    private String image;
    private String fee;

    public DocDetail(String id, String name, String specialization, String experience, String image, String fee) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.image = image;
        this.fee = fee;
    }

    public DocDetail() {

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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
