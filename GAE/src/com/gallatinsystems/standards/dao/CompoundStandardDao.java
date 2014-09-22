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

package com.gallatinsystems.standards.dao;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.CompoundStandard;
import com.gallatinsystems.standards.domain.DistanceStandard;
import com.gallatinsystems.standards.domain.Standard;
import com.gallatinsystems.standards.domain.CompoundStandard.RuleType;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.google.appengine.api.datastore.Key;

public class CompoundStandardDao extends BaseDAO<CompoundStandard> {

    public CompoundStandardDao() {
        super(CompoundStandard.class);
    }

    public List<CompoundStandard> listByChildStandard(Long id) {
        List<CompoundStandard> csListLeft = super.listByProperty(
                "standardIdLeft", id, "Long");
        List<CompoundStandard> csListRight = super.listByProperty(
                "standardIdRight", id, "Long");
        List<CompoundStandard> csList = new ArrayList<CompoundStandard>();
        if (csListLeft != null && csListLeft.size() > 0)
            csList.addAll(csListLeft);
        if (csListRight != null && csListRight.size() > 0)
            csList.addAll(csListRight);
        return csList;
    }

    public void delete(Long id) {
        CompoundStandard cs = this.getByKey(id);
        if (cs != null) {
            DistanceStandardDao dsDao = new DistanceStandardDao();
            if (dsDao.getByKey(cs.getStandardIdLeft()) != null) {
                DistanceStandard ds = dsDao.getByKey(cs.getStandardIdLeft());
                ds.setPartOfCompoundRule(false);
                dsDao.save(ds);
            }
            if (dsDao.getByKey(cs.getStandardIdRight()) != null) {
                DistanceStandard ds = dsDao.getByKey(cs.getStandardIdRight());
                ds.setPartOfCompoundRule(false);
                dsDao.save(ds);
            }
            StandardDao sDao = new StandardDao();
            if (sDao.getByKey(cs.getStandardIdLeft()) != null) {
                Standard s = sDao.getByKey(cs.getStandardIdLeft());
                s.setPartOfCompoundRule(false);
                sDao.save(s);
            }
            if (sDao.getByKey(cs.getStandardIdRight()) != null) {
                Standard s = sDao.getByKey(cs.getStandardIdRight());
                s.setPartOfCompoundRule(false);
                sDao.save(s);
            }
        }
    }

    public CompoundStandard save(CompoundStandard item) {
        StandardDao ssdao = new StandardDao();
        if (item.getStandardIdLeft() != null) {
            if ((item.getStandardLeftRuleType()
                    .equals(CompoundStandard.RuleType.NONDISTANCE))) {
                Standard left = ssdao.getByKey(item.getStandardIdLeft());
                item.setStandardLeft(left);
                item.setStandardIdLeft(left.getKey().getId());
                left.setPartOfCompoundRule(true);
                ssdao.save(left);
            } else if (item.getStandardLeftRuleType().equals(
                    CompoundStandard.RuleType.DISTANCE)) {
                DistanceStandardDao dsDao = new DistanceStandardDao();
                DistanceStandard ds = dsDao.getByKey(item.getStandardIdLeft());
                if (ds != null) {
                    ds.setPartOfCompoundRule(true);
                    item.setStandardLeft(ds);
                    item.setStandardIdLeft(ds.getKey().getId());
                    dsDao.save(ds);
                }
            }
        }
        if (item.getStandardIdRight() != null) {
            if (item.getStandardRightRuleType().equals(
                    CompoundStandard.RuleType.NONDISTANCE)) {
                Standard right = ssdao.getByKey(item.getStandardIdRight());
                item.setStandardRight(right);
                right.setPartOfCompoundRule(true);
                item.setStandardIdRight(right.getKey().getId());
                ssdao.save(right);
            } else if (item.getStandardRightRuleType().equals(
                    CompoundStandard.RuleType.DISTANCE)) {
                DistanceStandardDao dsDao = new DistanceStandardDao();
                DistanceStandard ds = dsDao.getByKey(item.getStandardIdRight());
                if (ds != null) {
                    ds.setPartOfCompoundRule(true);
                    item.setStandardRight(ds);
                    item.setStandardIdRight(ds.getKey().getId());
                    dsDao.save(ds);
                }
            }
        }
        return super.save(item);
    }

    public List<CompoundStandard> listByType(StandardType type) {
        List<CompoundStandard> csList = super.listByProperty("standardType",
                type, "String");
        StandardDao ssDao = new StandardDao();
        for (CompoundStandard item : csList) {
            if (item.getStandardIdLeft() != null) {
                if (item.getStandardLeftRuleType().equals(
                        CompoundStandard.RuleType.NONDISTANCE)) {
                    Standard left = ssDao.getByKey(item.getStandardIdLeft());
                    if (left != null)
                        item.setStandardLeft(left);
                } else if (item.getStandardLeftRuleType().equals(
                        CompoundStandard.RuleType.DISTANCE)) {
                    DistanceStandardDao dsDao = new DistanceStandardDao();
                    DistanceStandard leftDs = dsDao.getByKey(item
                            .getStandardIdLeft());
                    if (leftDs != null) {
                        item.setStandardLeft(leftDs);
                    }
                }
            }
            if (item.getStandardIdRight() != null) {
                if (item.getStandardRightRuleType().equals(RuleType.NONDISTANCE)) {
                    Standard right = ssDao.getByKey(item.getStandardIdRight());
                    if (right != null)
                        item.setStandardRight(right);
                } else if (item.getStandardRightRuleType().equals(
                        RuleType.DISTANCE)) {
                    DistanceStandardDao dsDao = new DistanceStandardDao();
                    DistanceStandard rightDs = dsDao.getByKey(item
                            .getStandardIdRight());
                    if (rightDs != null) {
                        item.setStandardRight(rightDs);
                    }
                }
            }
        }
        return csList;
    }

    @Override
    public CompoundStandard getByKey(Long id) {
        CompoundStandard item = super.getByKey(id);
        StandardDao ssDao = new StandardDao();
        item.setStandardLeft(ssDao.getByKey(item.getStandardIdLeft()));
        item.setStandardRight(ssDao.getByKey(item.getStandardIdRight()));
        return item;
    }

    @Override
    public CompoundStandard getByKey(Key key) {
        return this.getByKey(key.getId());
    }
}
