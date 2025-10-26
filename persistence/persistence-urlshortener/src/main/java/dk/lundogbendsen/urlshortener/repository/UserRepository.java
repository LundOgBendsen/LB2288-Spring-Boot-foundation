package dk.lundogbendsen.urlshortener.repository;

import dk.lundogbendsen.urlshortener.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>
{
}
