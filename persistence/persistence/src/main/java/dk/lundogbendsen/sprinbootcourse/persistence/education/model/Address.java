package dk.lundogbendsen.sprinbootcourse.persistence.education.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    String street;
    String city;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
