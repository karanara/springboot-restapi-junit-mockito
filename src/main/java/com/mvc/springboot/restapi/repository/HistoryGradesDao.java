package com.mvc.springboot.restapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mvc.springboot.restapi.models.HistoryGrade;

@Repository
public interface HistoryGradesDao extends CrudRepository<HistoryGrade, Integer> {

    public Iterable<HistoryGrade> findGradeByStudentId (int id);

    public void deleteByStudentId(int id);
}
