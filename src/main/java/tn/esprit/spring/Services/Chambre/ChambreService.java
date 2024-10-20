package tn.esprit.spring.Services.Chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    ChambreRepository repo;
    BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }

    @Override
    public Chambre findById(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Chambre with id " + id + " not found"));
    }


    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        return repo.countByTypeCAndBlocIdBloc(type, idBloc);
    }
    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        LocalDate[] currentAcademicYear = getCurrentAcademicYear();
        LocalDate dateDebutAU = currentAcademicYear[0];
        LocalDate dateFinAU = currentAcademicYear[1];

        return repo.findAll().stream()
                .filter(chambre -> isChambreInFoyerOfType(chambre, nomFoyer, type))
                .filter(chambre -> hasAvailablePlaces(chambre, dateDebutAU, dateFinAU))
                .toList();

    }

    private LocalDate[] getCurrentAcademicYear() {
        int year = LocalDate.now().getYear() % 100;
        LocalDate dateDebutAU;
        LocalDate dateFinAU;

        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }

        return new LocalDate[] {dateDebutAU, dateFinAU};
    }

    private boolean isChambreInFoyerOfType(Chambre chambre, String nomFoyer, TypeChambre type) {
        return chambre.getTypeC().equals(type) && chambre.getBloc().getFoyer().getNomFoyer().equals(nomFoyer);
    }

    private boolean hasAvailablePlaces(Chambre chambre, LocalDate dateDebutAU, LocalDate dateFinAU) {
        long numReservation = chambre.getReservations().stream()
                .filter(reservation -> !reservation.getAnneeUniversitaire().isBefore(dateDebutAU)
                        && !reservation.getAnneeUniversitaire().isAfter(dateFinAU))
                .count();

        switch (chambre.getTypeC()) {
            case SIMPLE:
                return numReservation == 0;
            case DOUBLE:
                return numReservation < 2;
            case TRIPLE:
                return numReservation < 3;
            default:
                return false;
        }
    }


    @Override
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() + " ayant une capacité " + b.getCapaciteBloc());
            if (!b.getChambres().isEmpty()) {
                log.info("La liste des chambres pour ce bloc: ");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() + " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("********************");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = ((double) repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100) / totalChambre;
        double pDouble = ((double) repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100) / totalChambre;
        double pTriple = ((double) repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100) / totalChambre;

        log.info("Nombre total des chambre: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à " + pTriple);

    }

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        // Début "récupérer l'année universitaire actuelle"
        LocalDate[] currentAcademicYear = getCurrentAcademicYear();
        LocalDate dateDebutAU = currentAcademicYear[0];
        LocalDate dateFinAU = currentAcademicYear[1];

        log.debug("Année universitaire actuelle : du " + dateDebutAU + " au " + dateFinAU);

        List<Chambre> chambres = repo.findAll();

        if (chambres.isEmpty()) {
            log.warn("Aucune chambre trouvée dans la base de données.");
            return;
        }

        for (Chambre c : chambres) {
            try {
                long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                        c.getIdChambre(), true, dateDebutAU, dateFinAU);
                log.debug("Chambre " + c.getNumeroChambre() + " a " + nbReservation + " réservation(s) validée(s).");

                int maxPlaces = getMaxPlacesForType(c.getTypeC());
                int placesDisponibles = maxPlaces - (int) nbReservation;

                if (placesDisponibles > 0) {
                    log.info("Le nombre de places disponibles pour la chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est " + placesDisponibles);
                } else {
                    log.info("La chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est complète");
                }
            } catch (Exception e) {
                log.error("Erreur lors du comptage des réservations pour la chambre " + c.getNumeroChambre(), e);
            }
        }
    }

    private int getMaxPlacesForType(TypeChambre type) {
        switch (type) {
            case SIMPLE:
                return 1;
            case DOUBLE:
                return 2;
            case TRIPLE:
                return 3;
            default:
                throw new IllegalArgumentException("Type de chambre non reconnu : " + type);
        }
    }

}
