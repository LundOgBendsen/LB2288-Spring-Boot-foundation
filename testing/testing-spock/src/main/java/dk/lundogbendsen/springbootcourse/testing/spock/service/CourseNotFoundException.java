package dk.lundogbendsen.springbootcourse.testing.spock.service;

public class CourseNotFoundException extends EducationException {
    public CourseNotFoundException(Long courseId) {
        super("Course not found with ID: " + courseId);
    }
}
