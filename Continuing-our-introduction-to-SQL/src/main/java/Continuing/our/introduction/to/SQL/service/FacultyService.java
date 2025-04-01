package Continuing.our.introduction.to.SQL.service;

import Continuing.our.introduction.to.SQL.model.Faculty;

public interface FacultyService {
    Faculty getFaculty(Long id);

    Faculty createFaculty(Faculty faculty);

    Faculty updateFaculty(Faculty faculty);

    void removeFaculty(Long id);
}
