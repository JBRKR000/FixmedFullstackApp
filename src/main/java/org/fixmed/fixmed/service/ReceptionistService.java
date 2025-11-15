package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.Receptionist;



import java.util.List;
import java.util.Optional;

public interface ReceptionistService {
    List<Receptionist> getReceptionistsByFacility(Long facilityId);
    Receptionist saveReceptionist(Receptionist receptionist);
    Optional<Facilities> getFacilityByReceptionistUserId(Long userId);
}