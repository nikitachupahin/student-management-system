package org.example.studentmanagementsystem.service.impl;

import jakarta.transaction.Transactional;
import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.entity.StudentGrade;
import org.example.studentmanagementsystem.logs.LogService;
import org.example.studentmanagementsystem.repository.StudentGradeRepository;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final LogService logService;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, StudentGradeRepository studentGradeRepository, LogService logService) {
        this.studentRepository = studentRepository;
        this.studentGradeRepository = studentGradeRepository;
        this.logService = logService;
    }

    @Override
    public List<Student> getAllStudents() {
        logService.log("Retrieving all students");
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        logService.log("Getting student by id " + id);
        return studentRepository.getReferenceById(id);
    }

    private boolean isValidSubject(String subject) {
        return !(subject.equals("fullName") || subject.equals("course") ||
                subject.equals("studentCardNumber") || subject.equals("majorCode") ||
                subject.equals("numberOfExams") || subject.equals("publicWorkParticipation") ||
                subject.equals("livingInDormitory"));
    }

    @Override
    public Student addStudent(Student student, Map<String, Object> grades) {

        if (studentRepository.findByStudentCardNumber(student.getStudentCardNumber()).isPresent()) {
            throw new IllegalArgumentException("Student card number already exists.");
        }

        student = studentRepository.save(student);

        grades.entrySet().removeIf(entry -> !isValidSubject(entry.getKey()));

        double totalScore = 0;
        int count = 0;

        for (Map.Entry<String, Object> entry : grades.entrySet()) {
            String subject = entry.getKey();
            Double gradeValue;

            try {
                if (entry.getValue() instanceof String) {
                    gradeValue = Double.parseDouble((String) entry.getValue());
                } else if (entry.getValue() instanceof Number) {
                    gradeValue = ((Number) entry.getValue()).doubleValue();
                } else {
                    throw new IllegalArgumentException("Invalid grade value for subject " + subject);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid grade value for subject " + subject);
            }

            if (gradeValue < 0 || gradeValue > 100) {
                throw new IllegalArgumentException("Grade for " + subject + " is out of range: " + gradeValue);
            }

            StudentGrade grade = new StudentGrade();
            grade.setStudent(student);
            grade.setCourse(student.getCourse());
            grade.setSubjectName(subject);
            grade.setGrade(gradeValue.floatValue());
            studentGradeRepository.save(grade);

            totalScore += gradeValue;
            count++;
        }

        student.setAverageScore(count > 0 ? totalScore / count : 0);
        studentRepository.save(student);

        logService.log("Adding student: " + student.getFullName() + " with calculated average score: " + student.getAverageScore());
        return student;
    }


    @Transactional
    @Override
    public Student updateStudent(Student student, Map<String, Object> grades) {
        if (student.getStudentCardNumber() == null) {
            throw new IllegalArgumentException("Student card number cannot be null.");
        }

        Student existingStudent = studentRepository.findById(student.getId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        boolean courseChanged = existingStudent.getCourse() != student.getCourse();
        existingStudent.setCourse(student.getCourse());
        existingStudent.setFullName(student.getFullName());

        List<StudentGrade> existingGrades = studentGradeRepository.findByStudentId(student.getId());
        Map<String, StudentGrade> gradeMap = existingGrades.stream()
                .collect(Collectors.toMap(StudentGrade::getSubjectName, g -> g));

        boolean gradesChanged = false;
        double totalScore = 0;
        int count = 0;
        List<StudentGrade> updatedGrades = new ArrayList<>();

        for (Map.Entry<String, Object> entry : grades.entrySet()) {
            String subject = entry.getKey();
            if (!isValidSubject(subject)) {
                continue;
            }

            Double gradeValue;
            try {
                gradeValue = entry.getValue() instanceof String
                        ? Double.parseDouble((String) entry.getValue())
                        : (Double) entry.getValue();
            } catch (NumberFormatException | ClassCastException e) {
                throw new IllegalArgumentException("Invalid grade value for subject " + subject);
            }

            StudentGrade existingGrade = gradeMap.get(subject);

            if (existingGrade == null) {
                gradesChanged = true;
                StudentGrade newGrade = new StudentGrade();
                newGrade.setStudent(existingStudent);
                newGrade.setCourse(existingStudent.getCourse());
                newGrade.setSubjectName(subject);
                newGrade.setGrade(gradeValue.floatValue());
                updatedGrades.add(newGrade);
            } else if (existingGrade.getGrade() != gradeValue.floatValue()) {
                gradesChanged = true;
                existingGrade.setGrade(gradeValue.floatValue());
                updatedGrades.add(existingGrade);
            }

            totalScore += gradeValue;
            count++;
        }

        List<StudentGrade> gradesToRemove = existingGrades.stream()
                .filter(grade -> !grades.containsKey(grade.getSubjectName()))
                .collect(Collectors.toList());
        if (!gradesToRemove.isEmpty()) {
            studentGradeRepository.deleteAll(gradesToRemove);
            gradesChanged = true;
        }

        if (gradesChanged) {
            studentGradeRepository.saveAll(updatedGrades);
        }

        if (courseChanged || gradesChanged) {
            existingStudent.setAverageScore(count > 0 ? totalScore / count : existingStudent.getAverageScore());
            studentRepository.save(existingStudent);
            logService.log("Updating student: " + existingStudent.getFullName() + " with new average score: " + existingStudent.getAverageScore());
        }

        return existingStudent;
    }




    @Transactional
    @Override
    public void deleteStudent(Long id) {
        logService.log("Deleting student with ID: " + id);

        studentGradeRepository.deleteByStudentId(id);

        studentRepository.deleteById(id);
    }

}
