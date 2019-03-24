package com.epam.brest.project.dao;

import com.epam.brest.project.model.QuestionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


public class QuestionItemDaoJdbcImpl implements QuestionItemDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionItemDaoJdbcImpl.class);

    private static final String QUESTION_ITEM_ID = "question_item_id";
    private static final String ANSWER = "answer";
    private static final String QUESTION_ID = "question_id";
    private static final String DESCRIPTION = "description";
    private static final String TEST_ID = "test_id";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${questionItem.selectAllQuestionItem}")
    private String selectAllQuestionItem;

    @Value("${questionItem.selectByQuestionItem}")
    private String selectByQuestionItem;

    @Value("${questionItem.selectAllQuestionItemByQuestionId}")
    private String selectAllQuestionItemByQuestionId;

    @Value("${questionItem.insertQuestionItem}")
    private String insertQuestionItem;

    @Value("${questionItem.existQuestionById}")
    private String existQuestionById;

    @Value("${questionItem.updateQuestionItem}")
    private String updateQuestionItem;

    @Value("${questionItem.deleteQuestionItem}")
    private String deleteQuestionItem;

    public QuestionItemDaoJdbcImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Stream<QuestionItem> findall() {
        LOGGER.warn("start findall()");
        List<QuestionItem> questionItems = namedParameterJdbcTemplate.query(selectAllQuestionItem, new QuestionItemRowMapper());
        return questionItems.stream();
    }

    @Override
    public Optional<QuestionItem> findById(Integer id) {
        LOGGER.warn("start findById()");
        QuestionItem questionItem = namedParameterJdbcTemplate.queryForObject(selectByQuestionItem,
                new MapSqlParameterSource(QUESTION_ITEM_ID, id), new QuestionItemRowMapper());
        return Optional.ofNullable(questionItem);
    }

    @Override
    public List<QuestionItem> findallQuestionItemByQuestionId(Integer id) {
        LOGGER.warn("start findallQuestionByTestId()");
        Map<String, Integer> map = new HashMap<>();
        map.put(TEST_ID, id);
        return namedParameterJdbcTemplate.query(selectAllQuestionItemByQuestionId,
                map, new QuestionItemRowMapper());
    }

    @Override
    public Optional<QuestionItem> add(QuestionItem questionItem) {
        LOGGER.warn("start add()");
        return Optional.of(questionItem)
                .map(this::insertQuestionItem)
                .orElseThrow(() -> new IllegalArgumentException("Enter exist question"));
    }



    private Optional<QuestionItem> insertQuestionItem(QuestionItem questionItem) {
        LOGGER.warn("start insertQuestionItem()");
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(ANSWER, questionItem.getAnswer());
        mapSqlParameterSource.addValue(QUESTION_ID, questionItem.getQuestionId());
        mapSqlParameterSource.addValue(DESCRIPTION, questionItem.getDescription());

        KeyHolder generatorKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(insertQuestionItem, mapSqlParameterSource, generatorKeyHolder);
        Map<String, Object> keyMap = generatorKeyHolder.getKeys();
        questionItem.setQuestionItemId((Integer) keyMap.get(QUESTION_ITEM_ID));
        return Optional.of(questionItem);
    }

    @Override
    public void update(QuestionItem questionItem) {
        LOGGER.warn("start update()");
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(ANSWER, questionItem.getAnswer());
        mapSqlParameterSource.addValue(DESCRIPTION, questionItem.getDescription());
        mapSqlParameterSource.addValue(QUESTION_ITEM_ID, questionItem.getQuestionItemId());
        Optional.of(namedParameterJdbcTemplate.update(updateQuestionItem, mapSqlParameterSource))
                .filter(this::countAffectedRow)
                .orElseThrow(() -> new IllegalArgumentException("Failed to update questionItem"));
    }


    private Boolean countAffectedRow(int numRowsUpdated){
        return numRowsUpdated > 0;
    }
    @Override
    public void delete(int id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(QUESTION_ITEM_ID, id);
        Optional.of(namedParameterJdbcTemplate.update(deleteQuestionItem, mapSqlParameterSource))
                .filter(this::countAffectedRow)
                .orElseThrow(() -> new RuntimeException("Failed to delete questionItem from DB"));
    }

    @Override
    public void batchDelete(List<List<QuestionItem>> questionItems) {
//        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[questionItems.size()];
//        for (int x =0; x<questionItems.size(); x++) {
//            List<QuestionItem> questionItemList = questionItems.get(x);
//            for (int j = 0; j < questionItemList.size(); j++) {
//                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
//                mapSqlParameterSource.addValue(QUESTION_ITEM_ID, questionItems.get(x).get(j).getQuestionItemId());
//                sqlParameterSources[x] = mapSqlParameterSource;
//            }
//        }
        Optional.of(namedParameterJdbcTemplate.batchUpdate(deleteQuestionItem,
                sqlParameterSource(questionItems, DELETE)));
    }

    @Override
    public void batchUpdate(List<List<QuestionItem>> questionItems) {
//        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[questionItems.size()];
//        for (int x =0; x<questionItems.size(); x++) {
//            List<QuestionItem> questionItemList = questionItems.get(x);
//            for (int j = 0; j < questionItemList.size(); j++) {
//                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
//                mapSqlParameterSource.addValue(QUESTION_ITEM_ID, questionItems.get(x).get(j).getQuestionItemId());
//                mapSqlParameterSource.addValue(DESCRIPTION, questionItems.get(x).get(j).getDescription());
////            mapSqlParameterSource.addValue(QUESTION_ID, questionItems.get(x).get(j).getQuestionId());
//                mapSqlParameterSource.addValue(ANSWER, questionItems.get(x).get(j).getAnswer());
//                sqlParameterSources[x] = mapSqlParameterSource;
//            }
//        }

        Optional.of(namedParameterJdbcTemplate.batchUpdate(updateQuestionItem,
                sqlParameterSource(questionItems, UPDATE)));
    }

    private SqlParameterSource[] sqlParameterSource (List<List<QuestionItem>> questionItems, String info){
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[questionItems.size()];
        for (int x =0; x<questionItems.size(); x++) {
            List<QuestionItem> questionItemList = questionItems.get(x);
            for (int j = 0; j < questionItemList.size(); j++) {
                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
                if (info.equals("update")){
                    mapSqlParameterSource.addValue(DESCRIPTION, questionItems.get(x).get(j).getDescription());
                    mapSqlParameterSource.addValue(ANSWER, questionItems.get(x).get(j).getAnswer());
                }
                mapSqlParameterSource.addValue(QUESTION_ITEM_ID, questionItems.get(x).get(j).getQuestionItemId());
                sqlParameterSources[x] = mapSqlParameterSource;
            }
        }
        return sqlParameterSources;
    }


    private class QuestionItemRowMapper implements RowMapper<QuestionItem> {
        @Override
        public QuestionItem mapRow(ResultSet resultSet, int i) throws SQLException {
            QuestionItem questionItem = new QuestionItem();
            questionItem.setQuestionItemId(resultSet.getInt(QUESTION_ITEM_ID));
            questionItem.setDescription(resultSet.getString(DESCRIPTION));
            questionItem.setAnswer(resultSet.getBoolean(ANSWER));
            questionItem.setQuestionId(resultSet.getInt(QUESTION_ID));
            return questionItem;
        }
    }
}
