package org.example.studentmanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "student_card_number", nullable = false, unique = true)
    private String studentCardNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "course", nullable = false)
    private int course;

    @Column(name = "major_code", nullable = false)
    private String majorCode;

    @Column(name = "average_score", nullable = false)
    private double averageScore;

    @Column(name = "public_work_participation", nullable = false)
    private boolean publicWorkParticipation;

    @Column(name = "number_of_exams", nullable = false)
    private int numberOfExams;

    @Column(name = "dormitory", nullable = false)
    private boolean livingInDormitory;

    public Student() {
    }

    public Student(String studentCardNumber, String fullName, int course, String majorCode, double averageScore, boolean publicWorkParticipation, int numberOfExams, boolean livingInDormitory) {
        this.studentCardNumber = studentCardNumber;
        this.fullName = fullName;
        this.course = course;
        this.majorCode = majorCode;
        this.averageScore = averageScore;
        this.publicWorkParticipation = publicWorkParticipation;
        this.numberOfExams = numberOfExams;
        this.livingInDormitory = livingInDormitory;
    }

    public long getId() {
        return id;
    }

    public String getStudentCardNumber() {
        return studentCardNumber;
    }

    public void setStudentCardNumber(String studentCardNumber) {
        this.studentCardNumber = studentCardNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public boolean isPublicWorkParticipation() {
        return publicWorkParticipation;
    }

    public void setPublicWorkParticipation(boolean publicWorkParticipation) {
        this.publicWorkParticipation = publicWorkParticipation;
    }

    public int getNumberOfExams() {
        return numberOfExams;
    }

    public void setNumberOfExams(int numberOfExams) {
        this.numberOfExams = numberOfExams;
    }

    public boolean isLivingInDormitory() {
        return livingInDormitory;
    }

    public void setLivingInDormitory(boolean livingInDormitory) {
        this.livingInDormitory = livingInDormitory;
    }
}
