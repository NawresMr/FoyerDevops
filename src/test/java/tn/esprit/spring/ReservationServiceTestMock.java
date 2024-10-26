package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class ReservationServiceTestMock {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-Bloc A-101-123456");
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(false);

        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation result = reservationService.addOrUpdate(reservation);

        assertNotNull(result);
        assertEquals(reservation.getIdReservation(), result.getIdReservation());
        verify(reservationRepository, times(1)).save(reservation);
    }



    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        // Arrange
        Long numChambre = 101L;
        long cin = 123456;

        Chambre chambre = new Chambre();
        chambre.setNumeroChambre(numChambre);
        chambre.setTypeC(TypeChambre.SIMPLE);  // Exemple d'attribut TypeChambre

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(cin);

        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-Bloc A-101-123456");
        reservation.setAnneeUniversitaire(LocalDate.now());
        reservation.setEstValide(true);

        when(chambreRepository.findByNumeroChambre(numChambre)).thenReturn(chambre);
        when(etudiantRepository.findByCin(cin)).thenReturn(etudiant);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Reservation result = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(numChambre, cin);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEstValide());
        assertEquals(reservation.getIdReservation(), result.getIdReservation());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }



}
