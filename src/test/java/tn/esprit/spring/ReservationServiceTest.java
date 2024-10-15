package tn.esprit.spring;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    private Chambre chambre;
    private Etudiant etudiant;

    @BeforeAll
    void setUp() {
        // Créer une chambre pour les tests
        chambre = new Chambre();
        chambre.setNumeroChambre(101);
        chambre.setTypeC(TypeChambre.SIMPLE); // Correcte ici
        chambreRepository.save(chambre);

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
        chambreRepository.deleteAll();
    }

    @Test
    @Rollback(value = false) // Pour ne pas annuler les transactions
    public void testAddOrUpdateReservation() {
        // Créer une réservation
        Reservation reservation = new Reservation();
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(false);
        reservation.setIdReservation("2023/2024-Bloc A-101-123456");
        reservation.getEtudiants().add(etudiant);

        // Ajouter la réservation
        Reservation savedReservation = reservationService.addOrUpdate(reservation);

        // Vérifier que la réservation est ajoutée
        assertNotNull(savedReservation);
        assertEquals(savedReservation.getIdReservation(), reservation.getIdReservation());
    }

    @Test
    @Rollback(value = false)
    public void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        // Ajouter une réservation et l'assigner à la chambre et à l'étudiant
        Long numChambre = chambre.getNumeroChambre();
        long cin = etudiant.getCin();

        Reservation reservation = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(numChambre, cin);

        // Vérifier que la réservation a été ajoutée
        assertNotNull(reservation);
        assertTrue(reservation.isEstValide());
        assertTrue(reservation.getEtudiants().contains(etudiant));
    }

    @Test
    @Rollback(value = false)
    public void testAnnulerReservation() {
        // Créer une réservation
        Reservation reservation = new Reservation();
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(true);
        reservation.setIdReservation("2023/2024-Bloc A-101-123456");
        reservation.getEtudiants().add(etudiant);
        reservationService.addOrUpdate(reservation);

        // Annuler la réservation
        String result = reservationService.annulerReservation(etudiant.getCin());

        // Vérifier que la réservation est annulée
        assertTrue(result.contains("annulée avec succès"));
        assertFalse(reservation.isEstValide());
    }

    @Test
    @Rollback(value = false)
    public void testAnnulerReservations() {
        // Créer plusieurs réservations
        for (int i = 0; i < 3; i++) {
            Reservation reservation = new Reservation();
            reservation.setAnneeUniversitaire(LocalDate.now());
            reservation.setEstValide(true);
            reservation.setIdReservation("2023/2024-Bloc A-101-" + (123456 + i));
            reservation.getEtudiants().add(etudiant);
            reservationService.addOrUpdate(reservation);
        }

        // Annuler toutes les réservations
        reservationService.annulerReservations();

        // Vérifier que toutes les réservations sont annulées
        int count = reservationRepository.countByAnneeUniversitaireBetween(LocalDate.now(), LocalDate.now().plusDays(1));
        assertEquals(0, count);
    }


}
