package com.mvc.springboot.restapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mvc.springboot.restapi.models.CollegeStudent;

@Repository
public interface StudentDao extends CrudRepository<CollegeStudent, Integer> {

    public CollegeStudent findByEmailAddress(String emailAddress);
}
