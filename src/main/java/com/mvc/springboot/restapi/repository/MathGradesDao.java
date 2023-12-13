package com.mvc.springboot.restapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mvc.springboot.restapi.models.MathGrade;

@Repository
public interface MathGradesDao extends CrudRepository<MathGrade, Integer> {

    public Iterable<MathGrade> findGradeByStudentId (int id);

    public void deleteByStudentId(int id);
}
