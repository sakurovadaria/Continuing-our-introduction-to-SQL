package Continuing.our.introduction.to.SQL.service;

import Continuing.our.introduction.to.SQL.exception.FacultyNotFoundException;
import Continuing.our.introduction.to.SQL.model.Faculty;
import Continuing.our.introduction.to.SQL.repository.FacultyRepository;
import org.springframework.stereotype.Service;

@Service
public class FacultyServiceImpl implements FacultyService{


    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }


    @Override
    public Faculty getFaculty(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(()-> new FacultyNotFoundException("Факультет не найден"));
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Override
    public Faculty updateFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Override
    public void removeFaculty(Long id) {
        facultyRepository.deleteById(id);
    }
}