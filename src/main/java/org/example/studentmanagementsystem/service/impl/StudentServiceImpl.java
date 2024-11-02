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
        return !(subject.equals("fullName") || subject.equals("course") || subject.equals("recordBookNumber") ||
                subject.equals("studentCardNumber") || subject.equals("isExam") || subject.equals("groupCode") ||
                subject.equals("numberOfExams") || subject.equals("publicWorkParticipation") ||
                subject.equals("livingInDormitory"));
    }


    @Override
    public Student addStudent(Student student, Map<String, Object> grades) {
        if (studentRepository.findByRecordBookNumber(student.getRecordBookNumber()).isPresent()) {
            throw new IllegalArgumentException("Record Book Number already exists.");
        }

        if (studentRepository.findByStudentCardNumber(student.getStudentCardNumber()).isPresent()) {
            throw new IllegalArgumentException("Student card number already exists.");
        }

        student = studentRepository.save(student);
        grades.entrySet().removeIf(entry -> !isValidSubject(entry.getKey()));

        double totalScore = 0;
        int count = 0;

        for (Map.Entry<String, Object> entry : grades.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("grade_")) {
                String subject = key.substring(6);
                Double gradeValue;

                try {
                    gradeValue = entry.getValue() instanceof String
                            ? Double.parseDouble((String) entry.getValue())
                            : ((Number) entry.getValue()).doubleValue();
                } catch (NumberFormatException | ClassCastException e) {
                    throw new IllegalArgumentException("Invalid grade value for subject " + subject);
                }

                if (gradeValue < 0 || gradeValue > 5) {
                    throw new IllegalArgumentException("Grade for " + subject + " is out of range: " + gradeValue);
                }

                boolean isExam = Boolean.parseBoolean(grades.get("isExam_" + subject).toString());

                StudentGrade grade = new StudentGrade();
                grade.setStudent(student);
                grade.setCourse(student.getCourse());
                grade.setSubjectName(subject);
                grade.setGrade(gradeValue.floatValue());
                grade.setExam(isExam);
                studentGradeRepository.save(grade);

                totalScore += gradeValue;
                count++;
            }
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

        // Find the existing student
        Student existingStudent = studentRepository.findById(student.getId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // Check if the course has changed
        boolean courseChanged = existingStudent.getCourse() != student.getCourse();

        // Update student details
        existingStudent.setStudentCardNumber(student.getStudentCardNumber());
        existingStudent.setCourse(student.getCourse());
        existingStudent.setFullName(student.getFullName());
        existingStudent.setNumberOfExams(student.getNumberOfExams());
        existingStudent.setLivingInDormitory(student.isLivingInDormitory());
        existingStudent.setPublicWorkParticipation(student.isPublicWorkParticipation());
        existingStudent.setGroupCode(student.getGroupCode());

        // Get exam subjects and all subjects for the updated course
        List<String> examSubjects = getExamSubjectsForCourse(student.getCourse());
        List<String> courseSubjects = getAllSubjectsForCourse(student.getCourse());

        // Retrieve existing grades for the student
        List<StudentGrade> existingGrades = studentGradeRepository.findByStudentId(student.getId());
        Map<String, StudentGrade> existingGradesMap = existingGrades.stream()
                .collect(Collectors.toMap(StudentGrade::getSubjectName, grade -> grade));

        double totalScore = 0;
        int count = 0;

        List<StudentGrade> updatedGrades = new ArrayList<>();

        for (Map.Entry<String, Object> entry : grades.entrySet()) {
            String key = entry.getKey();


            if (key.startsWith("isExam_")) {
                continue;
            }

            String subject = key.startsWith("grade_") ? key.substring(6) : key;

            if (!courseSubjects.contains(subject)) {
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

            boolean isExam = examSubjects.contains(subject);

            StudentGrade grade = existingGradesMap.get(subject);
            if (grade == null) {
                grade = new StudentGrade();
                grade.setStudent(existingStudent);
                grade.setSubjectName(subject);
            }

            grade.setGrade(gradeValue.floatValue());
            grade.setCourse(existingStudent.getCourse());
            grade.setExam(isExam);

            updatedGrades.add(grade);

            totalScore += gradeValue;
            count++;
        }


        for (StudentGrade existingGrade : existingGrades) {
            if (!courseSubjects.contains(existingGrade.getSubjectName())) {
                studentGradeRepository.delete(existingGrade);
            }
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



    private List<String> getExamSubjectsForCourse(int course) {
        switch (course) {
            case 1:
                return List.of("CS", "ASD", "Math");
            case 2:
                return List.of("ASD", "SA");
            case 3:
                return List.of("ML", "Math");
            case 4:
                return List.of("ML", "CS");
            default:
                return new ArrayList<>();
        }
    }

    private List<String> getAllSubjectsForCourse(int course) {
        switch (course) {
            case 1:
                return List.of("CS", "ASD", "English", "Math", "SA");
            case 2:
                return List.of("CS", "ASD", "English", "SA");
            case 3:
                return List.of("CS", "ML", "English", "Math", "SA");
            case 4:
                return List.of("CS", "ML", "English");
            default:
                return new ArrayList<>();
        }
    }




    @Override
    public List<StudentGrade> getGradesByStudentId(Long studentId) {
        logService.log("Getting grades by student id");
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
        logService.log("Getting all Students grouped by group Code");
        List<Student> students = studentRepository.findAllByOrderByFullNameAsc();
        return students.stream().collect(Collectors.groupingBy(Student::getGroupCode));
    }

    @Override
    public Map<String, Double> getAverageScoresByGroup() {
        logService.log("Getting avg score by group");
        List<Student> students = studentRepository.findAll();

        return students.stream()
                .collect(Collectors.groupingBy(
                        Student::getGroupCode,
                        Collectors.averagingDouble(Student::getAverageScore)
                ));
    }

    @Override
    public List<Student> getStudentsForExpulsion() {
        logService.log("Getting students for expulsion");
        List<Student> students = studentRepository.findAll();

        return students.stream()
                .filter(student -> {
                    long failedSubjectsCount = studentGradeRepository.findByStudentId(student.getId()).stream()
                            .filter(grade -> grade.getGrade() < 3)
                            .count();
                    return failedSubjectsCount > 2;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Student getStudentByRecordBookNumber(int recordBookNumber) {
        logService.log("Getting students by record number");
        return studentRepository.findByRecordBookNumber(recordBookNumber).orElse(null);
    }




}
