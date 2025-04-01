package Continuing.our.introduction.to.SQL.service;

import Continuing.our.introduction.to.SQL.exception.StudentNotFoundException;
import Continuing.our.introduction.to.SQL.model.Student;
import Continuing.our.introduction.to.SQL.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(()-> new StudentNotFoundException("Студент не найден"));
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Студент не найден"));

        existingStudent.setAge(student.getAge());
        existingStudent.setName(student.getName());

        return studentRepository.save(existingStudent);
    }


    @Override
    public Long countAllStudents() {
        return studentRepository.count();
    }

    @Override
    public Double averageAge() {
        return studentRepository.averageAge();
    }

    @Override
    public List<Student> getLastFiveStudents() {
        return studentRepository.findTop5ByOrderByIdDesc();
    }


    @Override
    public void removeStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
