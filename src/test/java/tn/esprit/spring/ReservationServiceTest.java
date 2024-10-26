package tn.esprit.spring;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ReservationServiceTest {


}
