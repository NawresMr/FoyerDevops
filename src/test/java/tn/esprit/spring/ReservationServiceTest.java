package tn.esprit.spring;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
 class ReservationServiceTest {

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
     void testAddOrUpdateReservation() {
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






}
