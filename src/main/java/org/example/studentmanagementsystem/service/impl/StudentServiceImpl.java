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
                subject.equals("studentCardNumber") || subject.equals("groupCode") ||
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
        existingStudent.setNumberOfExams(student.getNumberOfExams());
        existingStudent.setLivingInDormitory(student.isLivingInDormitory());
        existingStudent.setPublicWorkParticipation(student.isPublicWorkParticipation());
        existingStudent.setGroupCode(student.getGroupCode());

        List<StudentGrade> existingGrades = studentGradeRepository.findByStudentId(student.getId());
        studentGradeRepository.deleteAll(existingGrades);

        List<StudentGrade> updatedGrades = new ArrayList<>();
        double totalScore = 0;
        int count = 0;

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


            StudentGrade newGrade = new StudentGrade();
            newGrade.setStudent(existingStudent);
            newGrade.setCourse(existingStudent.getCourse());
            newGrade.setSubjectName(subject);
            newGrade.setGrade(gradeValue.floatValue());
            updatedGrades.add(newGrade);

            totalScore += gradeValue;
            count++;
        }


        if (!updatedGrades.isEmpty()) {
            studentGradeRepository.saveAll(updatedGrades);
        }


        if (courseChanged || !updatedGrades.isEmpty()) {
            existingStudent.setAverageScore(count > 0 ? totalScore / count : existingStudent.getAverageScore());
            studentRepository.save(existingStudent);
            logService.log("Updating student: " + existingStudent.getFullName() + " with new average score: " + existingStudent.getAverageScore());
        }

        return existingStudent;
    }


    @Override
    public List<StudentGrade> getGradesByStudentId(Long studentId) {
        return studentGradeRepository.findByStudentId(studentId);
    }

    @Transactional
    @Override
    public void deleteStudent(Long id) {
        logService.log("Deleting student with ID: " + id);

        studentGradeRepository.deleteByStudentId(id);

        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> getStudentsWithAllGradesOfFive() {
        logService.log("Retrieving all students on Increased Scholarship");
        return studentRepository.findAll().stream()
                .filter(student -> studentGradeRepository.findByStudentId(student.getId()).stream()
                        .allMatch(grade -> grade.getGrade() == 5))
                .collect(Collectors.toList());
    }
    @Override
    public List<Student> getStudentsOnStandardScholarship() {
        logService.log("Retrieving all students on Scholarship");

        return studentRepository.findAll().stream()
                .filter(student -> {
                    List<StudentGrade> grades = studentGradeRepository.findByStudentId(student.getId());

                    // Condition 1: Exclude students with only grades of 5
                    boolean hasOnlyFives = grades.stream().allMatch(grade -> grade.getGrade() == 5);
                    if (hasOnlyFives) {
                        return false;
                    }

                    // Condition 2: Exclude students with only grades of 4
                    boolean hasOnlyFours = grades.stream().allMatch(grade -> grade.getGrade() == 4);
                    if (hasOnlyFours) {
                        return false;
                    }

                    // Condition 3: Check if there are any grades below 3; if so, exclude the student
                    boolean hasGradesBelowThree = grades.stream().anyMatch(grade -> grade.getGrade() < 3);
                    if (hasGradesBelowThree) {
                        return false;
                    }

                    // Condition 4: Check if there is a mix of grades 4 and 5 (without any other grades)
                    boolean hasFourAndFiveOnly = grades.stream().allMatch(grade -> grade.getGrade() == 4 || grade.getGrade() == 5);
                    if (hasFourAndFiveOnly) {
                        return true;
                    }

                    // Condition 5: Check if there is only one grade of 3, and all others are 4 or 5
                    long countOfThree = grades.stream().filter(grade -> grade.getGrade() == 3).count();
                    boolean allOthersAreFourOrFive = grades.stream()
                            .filter(grade -> grade.getGrade() != 3)
                            .allMatch(grade -> grade.getGrade() == 4 || grade.getGrade() == 5);

                    // Return true if there is exactly one grade of 3, all others are 4 or 5, and student participates in public work
                    return countOfThree == 1 && allOthersAreFourOrFive && student.isPublicWorkParticipation();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Student>> getAllStudentsGroupedByGroupCode() {
        List<Student> students = studentRepository.findAllByOrderByFullNameAsc();
        return students.stream().collect(Collectors.groupingBy(Student::getGroupCode));
    }

    @Override
    public Map<String, Double> getAverageScoresByGroup() {
        List<Student> students = studentRepository.findAll();

        return students.stream()
                .collect(Collectors.groupingBy(
                        Student::getGroupCode,
                        Collectors.averagingDouble(Student::getAverageScore)
                ));
    }

    @Override
    public List<Student> getStudentsForExpulsion() {
        List<Student> students = studentRepository.findAll();

        return students.stream()
                .filter(student -> {
                    long failedSubjectsCount = studentGradeRepository.findByStudentId(student.getId()).stream()
                            .filter(grade -> grade.getGrade() < 3) // assuming grade < 3 is a failing grade (e.g., 2)
                            .count();
                    return failedSubjectsCount > 2; // More than 2 failing grades
                })
                .collect(Collectors.toList());
    }
}
