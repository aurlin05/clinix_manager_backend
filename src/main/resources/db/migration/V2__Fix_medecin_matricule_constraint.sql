-- Migration: Fix médecin matricule constraint
-- Description: Change matricule constraint from global unique to composite unique (clinic_id, matricule)

-- Supprimer la contrainte unique existante sur matricule
ALTER TABLE medecins DROP CONSTRAINT IF EXISTS uk_n9vyaarlygm644qcn9xneds69;
ALTER TABLE medecins DROP CONSTRAINT IF EXISTS medecins_matricule_key;

-- Ajouter la nouvelle contrainte composite unique sur (clinic_id, matricule)
ALTER TABLE medecins ADD CONSTRAINT uk_medecin_clinic_matricule UNIQUE (clinic_id, matricule);
