package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.entity.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {
    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student addStudent(Student student, Map<String, Object> grades);

    void deleteStudent(Long id);

    Student updateStudent(Student student, Map<String, Object> grades);
}

