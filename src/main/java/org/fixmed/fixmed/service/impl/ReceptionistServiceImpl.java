package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;

import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.Receptionist;
import org.fixmed.fixmed.repository.ReceptionistRepository;
import org.fixmed.fixmed.service.ReceptionistService;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceptionistServiceImpl implements ReceptionistService {
    private final ReceptionistRepository repository;

    @Override
    public List<Receptionist> getReceptionistsByFacility(Long facilityId) {
        return repository.findByFacilityId(facilityId);
    }

    @Override
    public Receptionist saveReceptionist(Receptionist receptionist) {
        return repository.save(receptionist);
    }

    @Override
    public Optional<Facilities> getFacilityByReceptionistUserId(Long userId) {
        return repository.findByUserId(userId).map(Receptionist::getFacility);
    }
}