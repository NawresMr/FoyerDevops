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
import java.util.NoSuchElementException;
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
