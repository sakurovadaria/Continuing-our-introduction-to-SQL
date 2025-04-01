package Continuing.our.introduction.to.SQL.repository;

import Continuing.our.introduction.to.SQL.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAgeBetween(Integer min, Integer max);

    long count();

    @Query("SELECT AVG(s.age) FROM Student s")
    Double averageAge();

    List<Student> findTop5ByOrderByIdDesc();
}

