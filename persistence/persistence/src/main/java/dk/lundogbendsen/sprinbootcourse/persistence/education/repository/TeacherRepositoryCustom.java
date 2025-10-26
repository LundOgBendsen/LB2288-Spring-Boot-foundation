package dk.lundogbendsen.sprinbootcourse.persistence.education.repository;


import dk.lundogbendsen.sprinbootcourse.persistence.education.model.Student;

import java.util.List;

public interface TeacherRepositoryCustom {
    List<Student> getStudents(String teacherName);
}
