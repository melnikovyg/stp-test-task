package com.company.scrumit.web.screens;

import com.company.scrumit.entity.LabourIntensity;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;


public class PlanningPokerDSStub extends CustomCollectionDatasource<LabourIntensity, UUID> {

    @Override
    protected Collection<LabourIntensity> getEntities(Map<String, Object> params) {
        return null;
    }
}