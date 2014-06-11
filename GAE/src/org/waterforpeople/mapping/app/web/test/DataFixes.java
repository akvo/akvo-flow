/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.web.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

public class DataFixes {
    private static Logger log = Logger.getLogger(DataFixes.class.getName());

    @SuppressWarnings("deprecation")
    public void fixQuestionAnswerStoreCollectionDate(HttpServletRequest req,
            HttpServletResponse resp) {
        fixDateAfterToday();
        log.log(Level.INFO, "Completed fixing collectiondates from the future");
        fixExactDate();
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        List<QuestionAnswerStore> qasList = qasDao
                .listByNotNullCollectionDateBefore(new Date("1/1/1900"), "all",
                        500);
        for (QuestionAnswerStore item : qasList) {
            SurveyInstance si = null;
            if (item != null && item.getSurveyInstanceId() != null) {
                try {
                    si = siDao.getByKey(item.getSurveyInstanceId());
                } catch (NullPointerException nex) {
                    log.log(Level.INFO,
                            "Caught a null pointer exception fetching "
                                    + item.toString()
                                    + " during QuestionAnswerStore cleanup will set default collectiondate");
                }
                if (si != null && si.getCollectionDate() != null) {
                    item.setCollectionDate(si.getCollectionDate());
                    qasDao.save(item);
                    log.log(Level.INFO,
                            "fixed: " + item.getKey()
                                    + " set collectionDate to: "
                                    + item.getCollectionDate()
                                    + " from surveyInstanceId: "
                                    + si.getKey().getId());
                } else {
                    item.setCollectionDate(new Date("1/1/1980"));
                    qasDao.save(item);
                    log.log(Level.INFO,
                            "fixed: "
                                    + item.getKey()
                                    + " set collectionDate to: "
                                    + item.getCollectionDate()
                                    + " Because I couldn't find a surveyinstance or it was missing a collectionDate");
                }
            } else {
                item.setCollectionDate(new Date("1/1/1980"));
                qasDao.save(item);
                log.log(Level.INFO, "fixed: " + item.getKey()
                        + " set collectionDate to: " + item.getCollectionDate()
                        + " SurveyInstanceId == null so setting default date");
            }
        }

    }

    private void fixExactDate() {
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        List<QuestionAnswerStore> qasList = qasDao.listByExactDateString();
        List<QuestionAnswerStore> newQasList = new ArrayList<QuestionAnswerStore>();

        for (QuestionAnswerStore item : qasList) {
            item.setCollectionDate(item.getCreatedDateTime());
            newQasList.add(item);
        }
        qasDao.save(newQasList);
    }

    private void fixDateAfterToday() {
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        List<QuestionAnswerStore> qasList = qasDao.listByNotNullCollectionDateAfter(new Date(),
                null, null);
        List<QuestionAnswerStore> newQasList = new ArrayList<QuestionAnswerStore>();
        log.log(Level.INFO, "Found " + qasList.size() + " qas records from the future.");
        for (QuestionAnswerStore item : qasList) {
            item.setCollectionDate(item.getCreatedDateTime());
            newQasList.add(item);
        }
        qasDao.save(newQasList);
        log.log(Level.INFO, "Fixed " + newQasList.size() + " from the future");
    }

    public void generateTestData() {
        for (int i = 0; i < 1000; i++) {
            SurveyInstanceDAO siDao = new SurveyInstanceDAO();
            SurveyInstance si = new SurveyInstance();
            si.setCollectionDate(new Date());
            si.setCommunity("test");
            siDao.save(si);

            QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setCollectionDate(new Date("1/1/1000"));
            qas.setSurveyInstanceId(si.getKey().getId());
            qasDao.save(qas);
        }
    }

}
