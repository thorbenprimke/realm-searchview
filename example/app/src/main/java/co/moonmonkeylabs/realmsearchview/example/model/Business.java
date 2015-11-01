package co.moonmonkeylabs.realmsearchview.example.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by thorben on 10/19/15.
 */
public class Business extends RealmObject {

    @PrimaryKey
    private int id;

    private String name;

    private float latitude;

    private float longitude;

    public Business(int id, String name, float latitude, float longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Business() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
