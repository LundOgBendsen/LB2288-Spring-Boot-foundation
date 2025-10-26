package dk.lundogbendsen.sprinbootcourse.persistence.relationships;

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Address;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Course;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.MultiValueEntity;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.manytomany.Order;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.manytomany.OrderItem;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.onetomany.Department;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.onetomany.Employee;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.onetoone.Rifle;
import dk.lundogbendsen.sprinbootcourse.persistence.education.model.onetoone.Soldier;
import dk.lundogbendsen.sprinbootcourse.persistence.education.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RelationshipTests {
    @Autowired
    EntityManager entityManager;

    @Autowired
    CourseRepository courseRepository;

    @Test
    public void testWhyListCollectionsAreSlow() {
        // Why should we use Set instead of List?
        // Because List collections preserve order of association
        // resulting in deletion of rows of association in case of changes to list...

        // Demo setup: create an order with 3 products:
        final OrderItem orderItem1 = new OrderItem();
        final OrderItem orderItem2 = new OrderItem();
        final OrderItem orderItem3 = new OrderItem();
        final Order order1 = new Order();
        order1.setOrderItems(Set.of(orderItem1, orderItem2, orderItem3));

        entityManager.persist(orderItem1);
        entityManager.persist(orderItem2);
        entityManager.persist(orderItem3);
        entityManager.persist(order1);
        entityManager.flush();
        entityManager.clear();

        // Fetch the order
        OrderItem orderItemToRemove = entityManager.find(OrderItem.class, orderItem3.getId());
        Order order = entityManager.find(Order.class, order1.getId());

        // Remove a product
        order.getOrderItems().remove(orderItemToRemove);
        entityManager.persist(order);
        entityManager.flush();
        entityManager.clear();
        // Notice the SQL in the log when flush() is called

        order = entityManager.find(Order.class, order1.getId());

        order.getOrderItems().add(orderItem3);
        entityManager.persist(order);
        entityManager.flush();
        entityManager.clear();

        order = entityManager.find(Order.class, order1.getId());

    }


    @Test
    public void testManyToMany() {
        OrderItem p1 = new OrderItem();
        OrderItem p2 = new OrderItem();
        Order o1 = new Order();
        Order o2 = new Order();

//        p1.setOrders(List.of(o1, o2));
//        p2.setOrders(List.of(o1, o2));
//        o1.setProducts(List.of(p1, p2));
//        o2.setProducts(List.of(p1, p2));

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.persist(o1);
        entityManager.persist(o2);
        entityManager.flush();
        entityManager.clear();

        final OrderItem orderItem1 = entityManager.find(OrderItem.class, p1.getId());
        final Order order2 = entityManager.find(Order.class, o2.getId());
    }

    @Test
    public void testManyToOne() {
        final Employee employee1 = new Employee();
        final Employee employee2 = new Employee();
        final Department department = new Department();

        department.setEmployees(List.of(employee1, employee2));
        employee1.setDepartment(department);
        employee2.setDepartment(department);

        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.persist(department);
        entityManager.flush();
        entityManager.clear();

        Employee emp1 = entityManager.find(Employee.class, employee1.getId());
        Department dep1 = entityManager.find(Department.class, department.getId());




    }


    @Test
    public void oneToMany() {
        final Employee employee1 = new Employee();
        final Employee employee2 = new Employee();
        final Department department1 = new Department();
        department1.setEmployees(List.of(employee1, employee2));


        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.persist(department1);
        entityManager.flush();
        entityManager.clear();

//        Employee emp1 = entityManager.find(Employee.class, employee1.getId());
        Department dep1 = entityManager.find(Department.class, department1.getId());
        dep1.getEmployees().get(1);

    }

    @Test
    public void oneToOne_not_enforced() {
        // When both entities have their own ONE relation and their own ID,
        // this is really just a special case of ONE-TO-MANY. No enforcement.
        // This is allowed: R1 -> S1 -> R2
        Soldier soldier = new Soldier();
        Rifle rifle1 = new Rifle();
        Rifle rifle2 = new Rifle();
        rifle1.setSoldier(soldier);
        rifle2.setSoldier(soldier);
        entityManager.persist(soldier);
        entityManager.persist(rifle1);
        // This works because there is no enforcement of the one-to-one cardinality.
        entityManager.persist(rifle2);
        entityManager.flush();
        entityManager.clear();

        // Both rifles has the same soldier.
        final Rifle r1 = entityManager.find(Rifle.class, rifle1.getId());
        final Rifle r2 = entityManager.find(Rifle.class, rifle2.getId());
        assertEquals(r1.getSoldier(), r2.getSoldier());
    }

    @Test
    public void oneToOneMappedBy() {
        // Add mappedBy="rifle" on Rifle's relation ensures that only 1 Rifle can belong to a soldier, and one Soldier belongs to a Rifle.
        // This is NOT allowed: R1 -> S1 -> R2

        // Soldier gets a Rifle:
        Rifle rifle1 = new Rifle();
        final Soldier soldier = new Soldier();
        soldier.setRifle(rifle1);
        entityManager.persist(soldier);
        entityManager.persist(rifle1);
        entityManager.flush();
        entityManager.clear();

        Soldier soldier1 = entityManager.find(Soldier.class, soldier.getId());
        rifle1 = entityManager.find(Rifle.class, rifle1.getId());

        assertEquals(soldier1, rifle1.getSoldier());

        // Soldier gets a NEW rifle:
        final Rifle rifleNew = new Rifle();
        soldier1.setRifle(rifleNew);
        entityManager.persist(rifleNew);
        entityManager.persist(soldier1);
        entityManager.flush();
        entityManager.clear();

        // Old rifle no longer has a Soldier (but NEW has):
        final Rifle rOld = entityManager.find(Rifle.class, rifle1.getId());
        final Rifle rNew = entityManager.find(Rifle.class, rifleNew.getId());
        soldier1 = entityManager.find(Soldier.class, soldier.getId());
        entityManager.merge(soldier1);
        assertNull(rOld.getSoldier());
        assertEquals(soldier1, rNew.getSoldier());

        // Conclusion: one-2-one is enforced...

    }

    @Test
    public void oneToOne_mapsId() {
        // Add mapsId on Rifle's relation and remove @GeneratedValue
        // Soldier gets a Rifle:
        // Rifle gives up its own ID and uses Soldiers.
        // Now the Rifle cannot exist without the Soldier, and the Soldier cannot have other Rifles
        // The Rifle might as well have been embedded onto the Soldiers table directly.
        // This is no longer possible: R1 -> S1 -> R2
        Rifle rifle1 = new Rifle();
        final Soldier soldier = new Soldier();
        rifle1.setSoldier(soldier);
        entityManager.persist(soldier);
        entityManager.persist(rifle1);
        entityManager.flush();
        entityManager.clear();

        Soldier soldier1 = entityManager.find(Soldier.class, soldier.getId());
        rifle1 = entityManager.find(Rifle.class, rifle1.getId());
        assertEquals(soldier1.getId(), rifle1.getId());
        assertEquals(soldier1, rifle1.getSoldier());

        // Soldier gets a NEW rifle:
        final Rifle rifleNew = new Rifle();
        soldier1.setRifle(rifleNew);
        // Will not work: the old rifle still exists with the same ID
        entityManager.persist(rifleNew);
        entityManager.persist(soldier1);
        entityManager.flush();
        entityManager.clear();


        // Conclusion: one-2-one is enforced...

    }

    @Test
    public void manyToMany() {

        final Student student = Student.builder().name("Student-" + 1).courses(new HashSet<>()).build();
        final Student student2 = Student.builder().name("Student-" + 2).courses(new HashSet<>()).build();
        for (int i=1; i<10; i++) {
            Course subject = Course.builder().subject("subject-" + i).points(1).students(new HashSet<>()).build();
            subject.getStudents().add(student);
            subject.getStudents().add(student2);
            entityManager.persist(subject);
        }

        entityManager.persist(student);
        entityManager.persist(student2);
        entityManager.flush();
        entityManager.clear();

        final Student student1 = entityManager.find(Student.class, student.getId());
        student1.getCourses().forEach(course -> {
            course.getStudents().remove(student1);
        });
//        courseRepository.saveAll(student.getCourses());
        entityManager.persist(student1);
        entityManager.flush();
        entityManager.clear();

    }

    @Test
    public void testMultiValueEntities() {
        final MultiValueEntity multiValueEntity = new MultiValueEntity();
        multiValueEntity.setStrings(List.of("string1", "string2", "string3"));
        final Address address = new Address();
        address.setCity("Cph");
        address.setStreet("Ægirs");
        multiValueEntity.setAddress(address);
        entityManager.persist(multiValueEntity);
        entityManager.flush();

    }
}
