package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.entity.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    void deleteByStudentId(Long studentId);

    List<StudentGrade> findByStudentId(long id);
    
}
