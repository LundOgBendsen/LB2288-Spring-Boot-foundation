package dk.lundogbendsen.health;

import lombok.Setter;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ServiceMaintenanceHealthIndicator implements HealthIndicator {

    @Setter
    private Boolean inMaintenance = false;

    public void setInMaintenance(Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }
    @Override
    public Health health() {
        if (!inMaintenance) {
            return Health.up().build();
        }
        return Health.down()
                .withDetail("Reason", "In Maintenance period")
                .build();
    }
}
