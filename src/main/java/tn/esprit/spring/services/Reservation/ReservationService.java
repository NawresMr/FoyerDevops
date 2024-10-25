package tn.esprit.spring.services.Reservation;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.Entities.Chambre;
import tn.esprit.spring.dao.Entities.Etudiant;
import tn.esprit.spring.dao.Entities.Reservation;
import tn.esprit.spring.dao.Repositories.ChambreRepository;
import tn.esprit.spring.dao.Repositories.EtudiantRepository;
import tn.esprit.spring.dao.Repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService implements IReservationService {
    ReservationRepository repo;
    ChambreRepository chambreRepository;
    EtudiantRepository etudiantRepository;

    @Override
    public Reservation addOrUpdate(Reservation r) {
        return repo.save(r);
    }

    @Override
    public List<Reservation> findAll() {
        return repo.findAll();
    }

    @Override
    public Reservation findById(String id) {
        // Retrieve the Optional<Reservation>
        Optional<Reservation> optionalReservation = repo.findById(id);

        // Check if the Reservation is present
        if (optionalReservation.isPresent()) {
            return optionalReservation.get(); // Safely access the Reservation object
        } else {
            // Handle the case where the Reservation is not found
            throw new EntityNotFoundException("Reservation not found with id: " + id);
            // Alternatively, you could return null or throw a custom exception
        }
    }


    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Reservation r) {
        repo.delete(r);
    }

    @Override
    public Reservation ajouterReservationEtAssignerAChambreEtAEtudiant(Long numChambre, long cin) {
        // Pour l’ajout de Réservation, l’id est un String et c’est la concaténation de "numeroChambre",
        // "nomBloc" et "cin". Aussi, l’ajout ne se fait que si la capacite maximale de la chambre est encore non atteinte.

        // Début "récuperer l'année universitaire actuelle"
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        // Fin "récuperer l'année universitaire actuelle"
        Reservation res = new Reservation();
        Chambre c = chambreRepository.findByNumeroChambre(numChambre);
        Etudiant e = etudiantRepository.findByCin(cin);
        boolean ajout = false;
        int numRes = chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(c.getIdChambre(), dateDebutAU, dateFinAU);
        log.info("Number of reservations: {}", numRes);

        switch (c.getTypeC()) {
            case SIMPLE:
                if (numRes < 1) {
                    ajout = true;
                } else {
                    log.info("Chambre simple remplie !");
                }
                break;
            case DOUBLE:
                if (numRes < 2) {
                    ajout = true;
                } else {
                    log.info("Chambre double remplie !");
                }
                break;
            case TRIPLE:
                if (numRes < 3) {
                    ajout = true;
                } else {
                    log.info("Chambre triple remplie !");
                }
                break;
        }
        if (ajout) {
            res.setEstValide(false);
            res.setAnneeUniversitaire(LocalDate.now());
            res.setIdReservation(dateDebutAU.getYear() + "/" + dateFinAU.getYear() + "-" + c.getBloc().getNomBloc() + "-" + c.getNumeroChambre() + "-" + e.getCin());
            res.getEtudiants().add(e);
            res.setEstValide(true);
            res = repo.save(res);
            c.getReservations().add(res);
            chambreRepository.save(c);
        }
        return res;
    }

    @Override
    public long getReservationParAnneeUniversitaire(LocalDate debutAnnee, LocalDate finAnnee) {
        return repo.countByAnneeUniversitaireBetween(debutAnnee, finAnnee);
    }

    @Override
    public String annulerReservation(long cinEtudiant) {
        Reservation r = repo.findByEtudiantsCinAndEstValide(cinEtudiant, true);
        Chambre c = chambreRepository.findByReservationsIdReservation(r.getIdReservation());
        c.getReservations().remove(r);
        chambreRepository.save(c);
        repo.delete(r);
        return "La réservation " + r.getIdReservation() + " est annulée avec succés";
    }

    @Override
    public void affectReservationAChambre(String idRes, long idChambre) {
        // Retrieve the reservation safely
        Reservation r = repo.findById(idRes)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found for id: " + idRes));

        // Retrieve the chambre safely
        Chambre c = chambreRepository.findById(idChambre)
                .orElseThrow(() -> new IllegalArgumentException("Chambre not found for id: " + idChambre));

        // Parent: Chambre, Child: Reservation
        // On affecte le child au parent
        c.getReservations().add(r);
        chambreRepository.save(c);
    }


    @Override
    public void annulerReservations(String idReservation) {
        // Check if the reservation exists
        Optional<Reservation> optionalReservation = repo.findById(idReservation);
        if (optionalReservation.isPresent()) {
            // If exists, delete the reservation
            repo.deleteById(idReservation);
        } else {
            // Handle case where reservation does not exist
            throw new NoSuchElementException("Reservation not found with ID: " + idReservation);
        }
    }




}
