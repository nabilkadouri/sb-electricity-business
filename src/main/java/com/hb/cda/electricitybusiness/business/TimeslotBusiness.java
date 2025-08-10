package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.controller.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.controller.dto.TimeslotResponse;

import java.util.List;

public interface TimeslotBusiness {
    List<TimeslotResponse> createMultipleTimeslots(List<TimeslotRequest> timeslotRequests) throws BusinessException;

    TimeslotResponse createTimeslot(TimeslotRequest request) throws BusinessException;

    List<TimeslotResponse> getAllTimeslots();

    TimeslotResponse getTimeslotById(Long id) throws BusinessException;

    TimeslotResponse updateTimeslot(Long id, TimeslotRequest request) throws BusinessException;

    void deleteTimeslot(Long id) throws BusinessException;

}
