package com.demoapp.hdfcdemo.newDemo;

import java.util.ArrayList;
import java.util.List;

public class DashboardItem {
    int img;
    String title;
    String description;
    boolean openSublayout;
    ArrayList<String> subItems;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOpenSublayout() {
        return openSublayout;
    }

    public void setOpenSublayout(boolean openSublayout) {
        this.openSublayout = openSublayout;
    }

    public ArrayList<String> getSubItems() {
        return subItems;
    }

    public void setSubItems(ArrayList<String> subItems) {
        this.subItems = subItems;
    }
}
