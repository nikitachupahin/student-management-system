package org.example.studentmanagementsystem.controller;

import org.example.studentmanagementsystem.entity.Student;
import org.example.studentmanagementsystem.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {
    private StudentService service;
    public StudentController(StudentService service) {
        this.service = service;
    }

    @RequestMapping("/")
    String getHome(Model model){
        model.addAttribute("students",service.getAllStudents());
        return "students";
    }

    @RequestMapping("/students")
    String getAllStudents(Model model){
        model.addAttribute("students",service.getAllStudents());
        return "students";
    }

    @RequestMapping("students/add")
    String addStudentForm(Model model){
        Student student = new Student();
        model.addAttribute("student",student);
        return "add-student";
    }

    @PostMapping("students")
    String addStudent(@ModelAttribute("student")Student student){
        service.addStudent(student);
        return "redirect:/students";
    }

    @RequestMapping("students/remove/{id}")
    String deleteStudent(@PathVariable("id")long id, Model model) throws Exception{
        service.deleteStudent(id);
        return "redirect:/students";
    }

    @GetMapping("students/update/{id}")
    String updateStudentForm(@PathVariable("id")Long id, Model model) throws Exception{
        model.addAttribute("student", service.getStudentById(id));
        return "update-student";
    }

    @PostMapping("students/{id}")
    String editStudent(@PathVariable Long id, @ModelAttribute("student") Student student) {
        Student existingStudent = service.getStudentById(id);

        existingStudent.setFullName(student.getFullName());
        existingStudent.setCourse(student.getCourse());
        existingStudent.setMajorCode(student.getMajorCode());
        existingStudent.setAverageScore(student.getAverageScore());
        existingStudent.setPublicWorkParticipation(student.isPublicWorkParticipation());
        existingStudent.setNumberOfExams(student.getNumberOfExams());
        existingStudent.setLivingInDormitory(student.isLivingInDormitory());

        service.updateStudent(existingStudent);
        return "redirect:/students";
    }


}
