package dk.lundogbendsen.sprinbootcourse.persistence.gamer;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
//@DataMongoTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GamerTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    GamerRepository gamerRepository;


    @Test
    public void persistGamer() {
        Gamer gamer = new Gamer();
        gamer.setAlias("The Master");
        gamer.setAvatar("ABC");
        entityManager.persist(gamer);
        entityManager.flush();
    }

    @Test
//    @Rollback(value = false)
    public void testGetGamer() {
        Gamer gamer = new Gamer();
        gamer.setAlias("The Master");
        gamer.setAvatar("ABC");
        gamerRepository.save(gamer);
        gamerRepository.flush();
        entityManager.clear();
        final Gamer gamer1 = gamerRepository.findById(gamer.getId()).get();
        assertEquals("The Master", gamer1.getAlias());
    }
}
