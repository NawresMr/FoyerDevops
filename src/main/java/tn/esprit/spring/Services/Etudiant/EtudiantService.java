package tn.esprit.spring.Services.Etudiant;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EtudiantService implements IEtudiantService {
    EtudiantRepository repo;

    @Override
    public Etudiant addOrUpdate(Etudiant e) {
        return repo.save(e);
    }

    @Override
    public List<Etudiant> findAll() {
        return repo.findAll();
    }

    @Override
    public Etudiant findById(long id) {
        Optional<Etudiant> optionalEtudiant = repo.findById(id);
        if (optionalEtudiant.isPresent()) {
            return optionalEtudiant.get();
        } else {
            // You can throw an exception or return null, depending on your requirements
            throw new EntityNotFoundException("Etudiant not found with id: " + id);
            // or return null; // If you prefer to return null instead
        }
    }


    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Etudiant e) {
        repo.delete(e);
    }
}
