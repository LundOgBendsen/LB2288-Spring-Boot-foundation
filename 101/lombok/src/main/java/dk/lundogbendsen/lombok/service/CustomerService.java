package dk.lundogbendsen.lombok.service;

import dk.lundogbendsen.lombok.model.Customer;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

@Service
@Slf4j
public class CustomerService {
    public Customer create(String name, Long id, String email, Date created) {
        final Customer customer = Customer.builder().id(id).name(name).created(created).email(email).build();

        log.info("Created customer {}", customer);

        return customer;
    }

    @SneakyThrows
    public void writeFile() {
        new File("/tmp").createNewFile();
    }


    static String readFirstLineFromFile(String path) throws IOException {
        @Cleanup BufferedReader br = new BufferedReader(new FileReader(path));
        return br.readLine();
    }
}
