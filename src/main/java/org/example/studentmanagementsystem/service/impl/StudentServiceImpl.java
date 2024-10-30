package org.example.studentmanagementsystem.service.impl;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.logs.LogService;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // letting it to be known as a service
public class StudentServiceImpl implements StudentService {

    // service implementation of StudentService

    // adding dependency injection of repository
    private StudentRepository studentRepository;
    private LogService logService;

    // constructor based injection
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.logService = new LogService();
    }


    @Override
    public List<Student> getAllStudents() {
        logService.log("Retrieving all students");
        return studentRepository.findAll(); //findAll() returns list of the <Entity>
    }
    @Override
    public Student getStudentById(Long id){ // get a reference student by id
        logService.log("Getting student by id ");
        return studentRepository.getReferenceById(id);
    }

    @Override
    public Student addStudent(Student student) {
        logService.log("Adding student: " + student.getFullName());
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {
        logService.log("Deleting student with ID: " + id);
        studentRepository.deleteById(id);
    }

    @Override
    public Student updateStudent(Student student) { // edit and update student if exist otherwise add new
        logService.log("Updating student: " + student.getFullName());
        return studentRepository.save(student);
    }

}