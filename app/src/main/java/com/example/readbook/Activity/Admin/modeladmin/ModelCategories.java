package com.example.readbook.Activity.Admin.modeladmin;

public class ModelCategories {

    String id,category,uid,uri;

    long timetamp;
    //contructor empty required for firebase

    public ModelCategories() {
    }
    //parametrized contructor

    public ModelCategories(String id, String category, String uid, long timetamp,String uri) {
        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timetamp = timetamp;
        this.uri= uri;

    }
   /* Geter-Setter*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(long timetamp) {
        this.timetamp = timetamp;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
