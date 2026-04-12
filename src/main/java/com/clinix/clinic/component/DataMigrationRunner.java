package com.clinix.clinic.component;

import com.clinix.clinic.model.Clinic;
import com.clinix.clinic.repository.ClinicRepository;
import com.clinix.clinic.repository.MedecinRepository;
import com.clinix.clinic.repository.PatientRepository;
import com.clinix.clinic.repository.RendezVousRepository;
import com.clinix.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Migration one-shot : assigne une clinique par défaut à toutes les données
 * créées avant l'implémentation du multi-tenant (clinic_id = NULL).
 * S'exécute au démarrage et ne fait rien si tout est déjà migré.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataMigrationRunner implements ApplicationRunner {

    private final UserRepository       userRepository;
    private final PatientRepository    patientRepository;
    private final MedecinRepository    medecinRepository;
    private final RendezVousRepository rdvRepository;
    private final ClinicRepository     clinicRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var unassignedUsers = userRepository.findByClinicIdIsNull();
        if (unassignedUsers.isEmpty()) return;

        log.info("[Migration] {} utilisateur(s) sans clinique — création d'une clinique par défaut...",
                unassignedUsers.size());

        Clinic defaultClinic = clinicRepository.save(
                Clinic.builder().nom("Clinique principale").build()
        );
        Long cid = defaultClinic.getId();

        int users   = userRepository.assignClinicToUnassigned(cid);
        int patients = patientRepository.assignClinicToUnassigned(cid);
        int medecins = medecinRepository.assignClinicToUnassigned(cid);
        int rdvs    = rdvRepository.assignClinicToUnassigned(cid);

        log.info("[Migration] Terminée — clinique #{} créée. Mis à jour : {} users, {} patients, {} médecins, {} RDV.",
                cid, users, patients, medecins, rdvs);
    }
}
