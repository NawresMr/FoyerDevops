package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation result = reservationService.addOrUpdate(reservation);

        assertNotNull(result);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_Success() {
        Long numChambre = 1L;
        long cin = 12345678L;

        // Créer la chambre mockée
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(numChambre);
        chambre.setTypeC(TypeChambre.SIMPLE);

        // Créer l'étudiant mocké
        Etudiant etudiant = new Etudiant();
        etudiant.setCin(cin);

        // Configurer les mocks pour retourner les valeurs appropriées
        when(chambreRepository.findByNumeroChambre(numChambre)).thenReturn(chambre);
        when(etudiantRepository.findByCin(cin)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(anyLong(), any(LocalDate.class), any(LocalDate.class))).thenReturn(0);

        // Créer une réservation avec les valeurs nécessaires
        Reservation reservation = new Reservation();
        reservation.setEstValide(true); // Assurez-vous que l'état est vrai

        // Mock de la méthode save pour retourner la réservation
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Appeler le service
        Reservation resultReservation = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(numChambre, cin);

        // Vérifier que la réservation n'est pas nulle et qu'elle est valide
        assertNotNull(resultReservation);
        assertTrue(resultReservation.isEstValide());

        // Vérifier que la méthode save a été appelée une fois
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(chambreRepository, times(1)).save(chambre);
    }


    @Test
    void testFindById() {
        String id = "res-1";
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.findById(id);

        assertNotNull(result);
        assertEquals(reservation, result);
        verify(reservationRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteById() {
        String id = "res-1";
        doNothing().when(reservationRepository).deleteById(id);

        reservationService.deleteById(id);

        verify(reservationRepository, times(1)).deleteById(id);
    }
}
