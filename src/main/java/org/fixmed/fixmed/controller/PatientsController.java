package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.fixmed.fixmed.model.Patients;
import org.fixmed.fixmed.model.dto.PatientSearchResult;
import org.fixmed.fixmed.service.PatientsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientsController {
    private final PatientsService patientsService;

    @PostMapping
    public ResponseEntity<String> savePatient(@RequestBody Patients patient) {
        Patients savedPatient = patientsService.savePatient(patient);
        return ResponseEntity.ok("Zarejestrowny");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patients> getPatientById(@PathVariable Long id) {
        Patients patient = patientsService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patients> updatePatient(@PathVariable Long id, @RequestBody Patients patient) {
        Patients updatedPatient = patientsService.updatePatient(id, patient);
        return ResponseEntity.ok(updatedPatient);
    }

    @GetMapping("/getPatientIdByUserId")
    public ResponseEntity<Long> getPatientIdByUserId(@RequestParam Long userId) {
        return patientsService.getPatientByUserId(userId)
                .map(patient -> ResponseEntity.ok(patient.getId()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientSearchResult>> searchPatients(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        List<PatientSearchResult> results = patientsService.searchPatients(query, limit);
        return ResponseEntity.ok(results);
    }
}