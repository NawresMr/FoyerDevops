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
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ReservationServiceTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    private ReservationService reservationService;

    @BeforeEach
    public void setup() {
        reservationService = new ReservationService(reservationRepository, chambreRepository, etudiantRepository);
        // Ajoutez des données de test
    }

    @Test
    public void testAddOrUpdateReservation() {
        // Créez un étudiant et une chambre
        Etudiant etudiant = new Etudiant(/* paramètres de construction */);
        etudiantRepository.save(etudiant);

        Chambre chambre = new Chambre(/* paramètres de construction */);
        chambreRepository.save(chambre);

        // Créez une réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation("123");
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(true);
        reservation.getEtudiants().add(etudiant);

        // Test de l'ajout
        Reservation savedReservation = reservationService.addOrUpdate(reservation);
        assertNotNull(savedReservation);
        assertEquals("123", savedReservation.getIdReservation());
    }



}
