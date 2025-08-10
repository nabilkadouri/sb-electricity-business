package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.controller.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationResponse;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChargingStationBusiness {
    ChargingStation createChargingStation (ChargingStation chargingStation);

    List<ChargingStation> getAllChargingStation();

    ChargingStation getChargingStationById (Long id);

    ChargingStation updateChargingStation(Long id, ChargingStation chargingStation);

    String updateChargingStationPicture(Long chargingStationId, MultipartFile file, String altText, boolean isMain);

    void deleteChargingStation(Long id);
}
