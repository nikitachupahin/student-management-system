package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.entity.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentCardNumber(String studentCardNumber);
    List<Student> findAllByOrderByFullNameAsc();

    // StudentRepository
    Optional<Student> findByRecordBookNumber(int recordBookNumber);


}