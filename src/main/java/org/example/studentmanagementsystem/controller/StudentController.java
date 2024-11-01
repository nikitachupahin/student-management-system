package org.example.studentmanagementsystem.controller;

import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class StudentController {

    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @RequestMapping("/students")
    String getAllStudents(Model model){
        model.addAttribute("students", service.getAllStudents());
        return "students";
    }

    @RequestMapping("students/add")
    String addStudentForm(Model model){
        model.addAttribute("student", new Student());
        return "add-student";
    }

    @PostMapping("students")
    String addStudent(@ModelAttribute("student") Student student, @RequestParam Map<String, Object> grades) {
        service.addStudent(student, grades);
        return "redirect:/students";
    }

    @RequestMapping("students/remove/{id}")
    String deleteStudent(@PathVariable("id") long id) {
        service.deleteStudent(id);
        return "redirect:/students";
    }

    @GetMapping("students/update/{id}")
    String updateStudentForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("student", service.getStudentById(id));
        return "update-student";
    }

    @PostMapping("students/{id}")
    String editStudent(@PathVariable Long id, @ModelAttribute("student") Student student,
                       @RequestParam Map<String, Object> grades,
                       @RequestParam(value = "redirect", required = false) String redirect) {

        Student existingStudent = service.getStudentById(id);

        if (student.getStudentCardNumber() == null || student.getStudentCardNumber().isEmpty()) {
            student.setStudentCardNumber(existingStudent.getStudentCardNumber());
        }

        service.updateStudent(student, grades);

        if ("IncreasedScholarship".equals(redirect)) {
            return "redirect:/IncreasedScholarship";
        }

        return "redirect:/students";
    }


    @GetMapping("students/{id}/grades")
    public String viewStudentGrades(@PathVariable("id") Long id, Model model) {
        Student student = service.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("grades", service.getGradesByStudentId(id));
        return "student-grades";
    }



    @RequestMapping("/IncrScholarship")
    public String getIncrScholarship(Model model) {
        model.addAttribute("students", service.getStudentsWithAllGradesOfFive());
        return "incr-scholarship";
    }


    @RequestMapping("/StandardScholarship")
    public String getStandardScholarship(Model model) {
        model.addAttribute("students", service.getStudentsOnStandardScholarship());
        return "standard-scholarship";
    }

    @RequestMapping("/studentsGrouped")
    String getAllStudentsASCByGroup(Model model) {
        model.addAttribute("studentsGrouped", service.getAllStudentsGroupedByGroupCode());
        return "students-by-groups";
    }

    @RequestMapping("/groupsGrades")
    public String getGroupsWithAverageScores(Model model) {
        Map<String, Double> averageScoresByGroup = service.getAverageScoresByGroup();
        model.addAttribute("averageScoresByGroup", averageScoresByGroup);
        return "groups-grades";
    }

    @RequestMapping("/expulsion")
    public String getStudentsForExpulsion(Model model) {
        List<Student> studentsForExpulsion = service.getStudentsForExpulsion();
        model.addAttribute("students", studentsForExpulsion);
        return "students-for-expulsion";
    }

}
