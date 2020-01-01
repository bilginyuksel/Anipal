package c.bilgin.anipal.Model;

import java.util.UUID;

public class FedAnimalLocation {
    private String userUUID,firstName,lastName, photoURL, description;
    private String postUUID,postPhotoURL;
    private String fedAnimalLocationUUID;
    private double latitude,longitude;
    public FedAnimalLocation(String userUUID,String firstName,
                             String lastName, String photoURL,
                             String postUUID, String postPhotoURL,
                             String description,
                             double latitude,double longitude){
        this.fedAnimalLocationUUID = UUID.randomUUID().toString();
        this.userUUID  = userUUID;
        this.firstName = firstName;
        this.description = description;
        this.lastName = lastName;
        this.photoURL = photoURL;
        this.postUUID = postUUID;
        this.postPhotoURL = postPhotoURL;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public FedAnimalLocation(){

    }

    public String getDescription() {
        return description;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getPostUUID() {
        return postUUID;
    }

    public String getPostPhotoURL() {
        return postPhotoURL;
    }

    public String getFedAnimalLocationUUID() {
        return fedAnimalLocationUUID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
