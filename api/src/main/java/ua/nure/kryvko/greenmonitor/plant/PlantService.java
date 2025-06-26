package ua.nure.kryvko.greenmonitor.plant;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PlantService {
    @Autowired
    private PlantRepository plantRepository;

    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    public Optional<Plant> getPlantById(Integer id) {
        return plantRepository.findById(id);
    }

    @Transactional
    public Plant savePlant(Plant plant) {
        return plantRepository.save(plant);
    }

    @Transactional
    public Plant updatePlant(Plant plant) {
        if(!plantRepository.existsById(plant.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return plantRepository.save(plant);
    }

    @Transactional
    public void deletePlant(Integer id) {
        if(!plantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        plantRepository.deleteById(id);
    }
}
