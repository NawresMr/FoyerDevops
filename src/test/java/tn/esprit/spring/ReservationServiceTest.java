package tn.esprit.spring;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    private Etudiant etudiant;

    @BeforeAll
    void setUp() {
        // Créer un étudiant pour les tests
        etudiant = new Etudiant();
        etudiant.setCin(123456);
        etudiant.setNomEt("Test");
        etudiant.setPrenomEt("User");
        etudiantRepository.save(etudiant);
    }

    @AfterAll
    void tearDown() {
        // Nettoyer les données après les tests
        reservationRepository.deleteAll();
        etudiantRepository.deleteAll();
    }

    @Test
    @Rollback(value = false)
    void testAddReservation() {
        // Créer une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-Bloc A-101-123456");
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(false);
        reservation.getEtudiants().add(etudiant); // Assurez-vous que la liste est bien initialisée dans la classe Reservation

        // Ajouter la réservation
        Reservation savedReservation = reservationService.addOrUpdate(reservation);

        // Vérifier que la réservation est ajoutée
        assertNotNull(savedReservation);
        assertEquals("2023/2024-Bloc A-101-123456", savedReservation.getIdReservation());
    }

    @Test
    void testGetReservationById() {
        // Créer et sauvegarder une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-Bloc A-101-123456");
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(false);
        reservation.getEtudiants().add(etudiant);
        reservationService.addOrUpdate(reservation);

        // Récupérer la réservation par ID
        Optional<Reservation> fetchedReservation = reservationRepository.findById("2023/2024-Bloc A-101-123456");

        // Vérifier que la réservation est présente et que les données sont correctes
        assertTrue(fetchedReservation.isPresent());
        assertEquals(reservation.getIdReservation(), fetchedReservation.get().getIdReservation());
    }

    @Test
    void testAnnulerReservation() {
        // Créer et sauvegarder une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-Bloc A-101-123456");
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(true);
        reservation.getEtudiants().add(etudiant);
        reservationService.addOrUpdate(reservation);

        // Annuler la réservation
        String result = reservationService.annulerReservation(etudiant.getCin());

        // Vérifier que la réservation est annulée
        assertTrue(result.contains("annulée avec succès"));
        Optional<Reservation> canceledReservation = reservationRepository.findById(reservation.getIdReservation());
        assertTrue(canceledReservation.isPresent());
        assertFalse(canceledReservation.get().isEstValide());
    }
}