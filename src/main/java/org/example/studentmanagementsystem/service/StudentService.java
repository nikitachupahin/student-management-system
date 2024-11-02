package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.entity.StudentGrade;

import java.util.List;
import java.util.Map;

public interface StudentService {
    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student addStudent(Student student, Map<String, Object> grades);

    void deleteStudent(Long id);

    List<StudentGrade> getGradesByStudentId(Long studentId);

    Student updateStudent(Student student, Map<String, Object> grades);

    List<Student> getStudentsWithAllGradesOfFive();

    List<Student> getStudentsOnStandardScholarship();

    Map<String, List<Student>> getAllStudentsGroupedByGroupCode();

    Map<String, Double> getAverageScoresByGroup();

    List<Student> getStudentsForExpulsion();
    Student getStudentByRecordBookNumber(int recordBookNumber);

}

