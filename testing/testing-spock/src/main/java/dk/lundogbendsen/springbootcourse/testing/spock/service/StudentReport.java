package dk.lundogbendsen.springbootcourse.testing.spock.service;

import java.util.List;

public record StudentReport(
        Long studentId,
        String studentName,
        List<String> enrolledCourses,
        int totalPoints,
        String academicStatus
) {
}
