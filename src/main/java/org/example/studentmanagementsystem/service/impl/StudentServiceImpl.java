package org.example.studentmanagementsystem.service.impl;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.logs.LogService;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;
    private LogService logService;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.logService = new LogService();
    }

    @Override
    public List<Student> getAllStudents() {
        logService.log("Retrieving all students");
        return studentRepository.findAll();
    }
    @Override
    public Student getStudentById(Long id){
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
    public Student updateStudent(Student student) {
        logService.log("Updating student: " + student.getFullName());
        return studentRepository.save(student);
    }

}