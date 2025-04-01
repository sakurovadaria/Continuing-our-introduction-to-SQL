package Continuing.our.introduction.to.SQL.repository;

import Continuing.our.introduction.to.SQL.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    @Query("SELECT a FROM Avatar a WHERE a.student.id = :studentId")
    Optional<Avatar> findByStudentId(@Param("studentId") Long studentId);
}

