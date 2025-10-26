package dk.lundogbendsen.sprinbootcourse.persistence.education.repository;

import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeacherRepositoryCustomImpl implements TeacherRepositoryCustom {
    @Autowired
    EntityManager entityManager;

    @Override
    public List<Student> getStudents(String teacherName) {
        final Query query = entityManager.createQuery("select s from Course c inner join c.students s where c.teacher.name=:name", Student.class);
        query.setParameter("name", teacherName);
        return query.getResultList();
    }

}
