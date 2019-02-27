package com.epam.brest.testing.dao;

import com.epam.brest.testing.model.Student;

import java.util.Optional;
import java.util.stream.Stream;

public interface StudentDao {
    Stream<Student> findall();

    Optional<Student> findById(final Integer id);

    Optional<Student> add(final Student student);

    void update(final Student student);

    void delete(final int id);

}