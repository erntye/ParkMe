package com.example.chiilek.parkme.entity.availabilityapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author chiilek
 * @since 24/3/2018.
 *
 * The main envelope to unwrap.
 */

public class Envelope {

    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public Item getItem() {
        return items.get(0);
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
