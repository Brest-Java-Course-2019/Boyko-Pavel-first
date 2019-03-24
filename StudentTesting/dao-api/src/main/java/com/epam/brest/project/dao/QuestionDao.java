package com.epam.brest.project.dao;

import com.epam.brest.project.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface QuestionDao {

    Stream<Question> findall();

    List<Question> findallQuestionByTestId(Integer id);

    Optional<Question> findById(final Integer id);

    Optional<Question> add(final Question question);

    void update(final Question question);

    void delete(final int id);

    void batchDelete(List<Question> questions);

    void batchUpdate(final List<Question> question);

}
