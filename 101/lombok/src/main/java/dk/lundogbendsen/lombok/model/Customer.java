package dk.lundogbendsen.lombok.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class Customer {
    private String name;
    private Long id;
    private String email;
    private Date created;
}
