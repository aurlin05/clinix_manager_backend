-- ============================================================
-- Script de données initiales - Clinix Manager
-- Exécuté automatiquement au démarrage (spring.jpa.ddl-auto=update)
-- ============================================================


-- ── Utilisateurs (mots de passe hashés BCrypt) ───────────────────────────
-- Mot de passe en clair : "admin123" → hashé BCrypt
INSERT INTO users (username, password, role) VALUES
('admin',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN'),
('medecin1','$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MEDECIN'),
('user1',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER')
ON CONFLICT DO NOTHING;

-- ── Médecins ─────────────────────────────────────────────────────────────
INSERT INTO medecins (nom, prenom, specialite, email, telephone, matricule, disponible) VALUES
('Benali',    'Ahmed',   'Cardiologie',       'a.benali@clinix.com',    '0661234567', 'MED-001', true),
('Chaoui',    'Fatima',  'Pédiatrie',         'f.chaoui@clinix.com',    '0662345678', 'MED-002', true),
('El Fassi',  'Youssef', 'Neurologie',        'y.elfassi@clinix.com',   '0663456789', 'MED-003', true),
('Lahlou',    'Sara',    'Dermatologie',      's.lahlou@clinix.com',    '0664567890', 'MED-004', false),
('Moussaoui', 'Karim',   'Médecine Générale', 'k.moussaoui@clinix.com', '0665678901', 'MED-005', true)
ON CONFLICT DO NOTHING;

-- ── Patients ──────────────────────────────────────────────────────────────
INSERT INTO patients (nom, prenom, date_naissance, cin, email, telephone, sexe, groupe_sanguin, antecedents) VALUES
('Alami',    'Mohamed',  '1985-03-15', 'AB123456', 'm.alami@email.com',    '0670123456', 'M', 'A+',  'Diabète type 2'),
('Benkirane','Aicha',    '1992-07-22', 'CD789012', 'a.benkirane@email.com','0671234567', 'F', 'O-',  NULL),
('Chraibi',  'Hassan',   '1978-11-08', 'EF345678', 'h.chraibi@email.com',  '0672345678', 'M', 'B+',  'Hypertension'),
('Douiri',   'Nadia',    '2000-01-30', 'GH901234', 'n.douiri@email.com',   '0673456789', 'F', 'AB+', NULL),
('El Amrani','Yassine',  '1965-09-12', 'IJ567890', 'y.elamrani@email.com', '0674567890', 'M', 'A-',  'Asthme chronique')
ON CONFLICT DO NOTHING;

-- ── Rendez-vous ───────────────────────────────────────────────────────────
INSERT INTO rendez_vous (date_heure, statut, motif, notes, patient_id, medecin_id) VALUES
(NOW() + INTERVAL '1 hour',  'EN_ATTENTE', 'Consultation cardiaque',      NULL,                   1, 1),
(NOW() + INTERVAL '3 hour',  'CONFIRME',   'Suivi pédiatrique',           'Apporter carnet santé', 2, 2),
(NOW() - INTERVAL '2 day',   'TERMINE',    'Contrôle neurologique',       'RDV de suivi dans 3M',  3, 3),
(NOW() - INTERVAL '5 day',   'ANNULE',     'Consultation dermatologie',   'Annulé par le patient', 4, 4),
(NOW() + INTERVAL '2 day',   'EN_ATTENTE', 'Médecine générale - bilan',   NULL,                   5, 5)
ON CONFLICT DO NOTHING;
