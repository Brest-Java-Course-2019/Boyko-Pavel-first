package com.epam.brest.testing.dao;
import com.epam.brest.testing.model.Subject;

import java.util.Optional;
import java.util.stream.Stream;

public interface SubjectDao {

    Stream<Subject> findall();

    Optional<Subject> findById(final Integer id);

    Optional<Subject> add(final Subject subject);

    void update(final Subject subject);

    void delete(final int id);
}