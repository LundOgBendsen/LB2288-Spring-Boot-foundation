package dk.lundogbendsen.sprinbootcourse.persistence.education.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

@Entity
public class MultiValueEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    Map<String, Address>  maps;

    @ElementCollection
    List<String> strings;

    @Embedded
    Address address;

    public Map<String, Address> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, Address> maps) {
        this.maps = maps;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address addresses) {
        this.address = addresses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }
}
