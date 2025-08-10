package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.model.LocationStation;

import java.util.List;

public interface LocationStationBusiness {
    LocationStation createLocationStation(LocationStation locationStation);

    List<LocationStation> getAllLocationStation();


    LocationStation getLocationStationById(Long id);

    LocationStation updateLocationStation(Long id, LocationStation locationStation);

    void deleteLocationsStation(Long id);
}
