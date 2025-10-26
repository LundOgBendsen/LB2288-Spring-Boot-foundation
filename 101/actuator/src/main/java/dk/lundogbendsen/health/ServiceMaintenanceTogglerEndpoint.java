package dk.lundogbendsen.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Endpoint(id = "service-maintenance")
@Component
public class ServiceMaintenanceTogglerEndpoint {

    @Autowired
    private ServiceMaintenanceHealthIndicator serviceMaintenanceHealthIndicator;

    @WriteOperation
    public void setInMaintenance(Boolean inMaintenance) {
        serviceMaintenanceHealthIndicator.setInMaintenance(inMaintenance);
    }
}
