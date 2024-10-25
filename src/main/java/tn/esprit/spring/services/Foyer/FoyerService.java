package tn.esprit.spring.services.Foyer;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.Entities.*;
import tn.esprit.spring.dao.Repositories.BlocRepository;
import tn.esprit.spring.dao.Repositories.FoyerRepository;
import tn.esprit.spring.dao.Repositories.UniversiteRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FoyerService implements IFoyerService {
    FoyerRepository repo;
    UniversiteRepository universiteRepository;
    BlocRepository blocRepository;

    @Override
    public Foyer addOrUpdate(Foyer f) {
        return repo.save(f);
    }

    @Override
    public List<Foyer> findAll() {
        return repo.findAll();
    }

    @Override
    public Foyer findById(long id) {
        Optional<Foyer> optionalFoyer = repo.findById(id);
        if (optionalFoyer.isPresent()) {
            return optionalFoyer.get();
        } else {
            // Vous pouvez lancer une exception ou retourner null selon vos besoins
            throw new EntityNotFoundException("Foyer not found with id: " + id);
            // ou return null; // Si vous préférez retourner null à la place
        }
    }


    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Foyer f) {
        repo.delete(f);
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite) {
        Foyer f = findById(idFoyer); // Child
        Universite u = universiteRepository.findByNomUniversite(nomUniversite); // Parent
        // On affecte le child au parent
        u.setFoyer(f);
        return universiteRepository.save(u);
    }

    @Override
    public Universite desaffecterFoyerAUniversite(long idUniversite) {
        Optional<Universite> optionalUniversite = universiteRepository.findById(idUniversite);

        if (optionalUniversite.isPresent()) {
            Universite u = optionalUniversite.get(); // Accéder à l'objet Universite seulement si présent
            u.setFoyer(null);
            return universiteRepository.save(u);
        } else {
            // Gérer le cas où l'Université n'est pas trouvée
            throw new EntityNotFoundException("Université not found with id: " + idUniversite);
            // Vous pouvez aussi choisir de retourner null ou une autre action selon vos besoins
        }
    }

    @Override
    public Foyer ajouterFoyerEtAffecterAUniversite(Foyer foyer, long idUniversite) {
        // Récuperer la liste des blocs avant de faire l'ajout
        List<Bloc> blocs = foyer.getBlocs();

        // Foyer est le child et universite est parent
        Foyer f = repo.save(foyer);

        // Retrieve the Optional<Universite>
        Optional<Universite> optionalUniversite = universiteRepository.findById(idUniversite);

        // Check if the Universite is present
        if (optionalUniversite.isPresent()) {
            Universite u = optionalUniversite.get(); // Safely access the Universite object

            // Foyer est le child et bloc est le parent
            // On affecte le child au parent
            for (Bloc bloc : blocs) {
                bloc.setFoyer(f); // Set the foyer to the new foyer object
                blocRepository.save(bloc); // Save the bloc
            }

            u.setFoyer(f); // Set the new foyer to the universite
            return universiteRepository.save(u).getFoyer(); // Return the updated foyer
        } else {
            // Handle the case where the Universite is not found
            throw new EntityNotFoundException("Université not found with id: " + idUniversite);
            // Alternatively, you could return null or throw a custom exception
        }
    }


    @Override
    public Foyer ajoutFoyerEtBlocs(Foyer foyer) {

        List<Bloc> blocs = foyer.getBlocs();
        foyer = repo.save(foyer);
        for (Bloc b : blocs) {
            b.setFoyer(foyer);
            blocRepository.save(b);
        }
        return foyer;
    }

}
