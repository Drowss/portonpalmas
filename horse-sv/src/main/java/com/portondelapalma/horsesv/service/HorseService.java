package com.portondelapalma.horsesv.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.portondelapalma.horsesv.dto.HorseDto;
import com.portondelapalma.horsesv.dto.HorseValidDto;
import com.portondelapalma.horsesv.model.Horse;
import com.portondelapalma.horsesv.repository.IHorseRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class HorseService implements IHorseService {

    @Autowired
    private IHorseRepository iHorseRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Horse createHorse(MultipartFile imageFile, String horseJson) throws JsonProcessingException {
        HorseValidDto horseValidDto = mapper.readValue(horseJson, HorseValidDto.class);
        String imagePath = s3Service.saveFile(imageFile);

        Horse horse = Horse.builder()
                .imagePath(imagePath)
                .breed(horseValidDto.getBreed())
                .description(horseValidDto.getDescription())
                .price(horseValidDto.getPrice())
                .bornOn(horseValidDto.getBornOn())
                .build();

        return iHorseRepository.save(horse);
    }

    @Override
    public ResponseEntity<String> deleteHorse(Long idHorse) throws URISyntaxException {
        Logger logger = LoggerFactory.getLogger(HorseService.class);

        if (idHorse == null || idHorse < 0) {
            logger.error("ID de caballo inválido: " + idHorse);
            throw new IllegalArgumentException("ID de caballo debe ser positivo: " + idHorse);
        }

        Horse horse = iHorseRepository.findById(idHorse).orElseThrow(() -> {
            logger.error("No se pudo encontrar un caballo con el ID: " + idHorse);
            return new NoSuchElementException("No se pudo encontrar un caballo con el ID: " + idHorse);
        });

        URI uri = new URI(horse.getImagePath());
        String path = uri.getPath();
        s3Service.deleteFile(path.substring(path.lastIndexOf('/') + 1));
        iHorseRepository.deleteById(idHorse);
        logger.info("Se ha eliminado la entidad correctamente");

        return ResponseEntity.ok("Se ha eliminado la entidad correctamente");
    }

    @Override
    public List<HorseDto> getAllHorses() {
        List<Horse> horses = iHorseRepository.findAll();
        return horses.stream()
                .map(horse -> modelMapper.map(horse, HorseDto.class)) //Each Horse is mapped as HorseDto
                .toList();
    }

    @Override
    public List<HorseDto> getAllByBreed(String breed) {
        List<Horse> horseDtos = iHorseRepository.getAllByBreed(breed);
        return horseDtos.stream()
                .map(horse -> modelMapper.map(horse, HorseDto.class)) //Each Horse is mapped as HorseDto
                .toList();
    }

    @Override
    public Horse putHorse(Long id, MultipartFile multipartFile, String horseJson) throws JsonProcessingException, URISyntaxException {
        Horse horse = iHorseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El caballo no fue encontrado " + id));

        if (horseJson != null) {
            mapper.registerModule(new JavaTimeModule());
            HorseDto horseDto = mapper.readValue(horseJson, HorseDto.class);

            Optional.ofNullable(horseDto.getBreed()).ifPresent(horse::setBreed);
            Optional.ofNullable(horseDto.getPrice()).ifPresent(horse::setPrice);
            Optional.ofNullable(horseDto.getDescription()).ifPresent(horse::setDescription);
            Optional.ofNullable(horseDto.getBornOn()).ifPresent(horse::setBornOn);
        }

        if (multipartFile != null) {
            String imagePath = s3Service.saveFile(multipartFile);
            URI uri = new URI(horse.getImagePath());
            String path = uri.getPath();
            s3Service.deleteFile(path.substring(path.lastIndexOf('/') + 1));
            horse.setImagePath(imagePath);
        }

        return iHorseRepository.save(horse);

    }

    @Override
    public Horse findHorseByIdHorseQuery(Long idHorse) {
        return (Horse) entityManager.createQuery("SELECT h FROM Horse h WHERE h.idHorse = :idHorse", Horse.class)
                .setParameter("idHorse", idHorse)
                .getSingleResult();
    }

    @Override
    public List<Horse> findAllHorsesSQL() {
        return entityManager.createQuery("SELECT h FROM Horse h", Horse.class).getResultList();
    }

    @Override
    public void saveHorseSQL(MultipartFile file, String horseJson) throws JsonProcessingException {
        HorseValidDto horseValidDto = mapper.readValue(horseJson, HorseValidDto.class);
        String imagePath = s3Service.saveFile(file);

        Horse horse = Horse.builder()
                .imagePath(imagePath)
                .breed(horseValidDto.getBreed())
                .description(horseValidDto.getDescription())
                .price(horseValidDto.getPrice())
                .bornOn(horseValidDto.getBornOn())
                .build();

        entityManager.persist(horse);
    }

    @Override
    public void updateHorseSQL(Long idHorse, MultipartFile file, String horseJson) throws JsonProcessingException, URISyntaxException {
        Horse horse = (Horse) entityManager.createQuery("SELECT h FROM Horse h WHERE h.idHorse = :idHorse", Horse.class)
                .setParameter("idHorse", idHorse)
                .getSingleResult();

        if (horseJson != null) {
            mapper.registerModule(new JavaTimeModule());
            HorseDto horseDto = mapper.readValue(horseJson, HorseDto.class);

            Optional.ofNullable(horseDto.getBreed()).ifPresent(horse::setBreed);
            Optional.ofNullable(horseDto.getPrice()).ifPresent(horse::setPrice);
            Optional.ofNullable(horseDto.getDescription()).ifPresent(horse::setDescription);
            Optional.ofNullable(horseDto.getBornOn()).ifPresent(horse::setBornOn);
            entityManager.createNativeQuery("UPDATE Horse SET breed = ?, price = ?, description = ?, bornOn = ? WHERE idHorse = ?")
                    .setParameter(1, horse.getBreed())
                    .setParameter(2, horse.getPrice())
                    .setParameter(3, horse.getDescription())
                    .setParameter(4, horse.getBornOn())
                    .setParameter(5, idHorse)
                    .executeUpdate();
        }

        if (file != null) {
            String imagePath = s3Service.saveFile(file);
            URI uri = new URI(horse.getImagePath());
            String path = uri.getPath();
            s3Service.deleteFile(path.substring(path.lastIndexOf('/') + 1));
            entityManager.createNativeQuery("UPDATE Horse SET imagePath = ? WHERE idHorse = ?")
                    .setParameter(1, imagePath)
                    .setParameter(2, idHorse)
                    .executeUpdate();
        }
    }

    @Override
    public void deleteHorseSQL(Long idHorse) {
        Horse horse = (Horse) entityManager.createQuery("SELECT h FROM Horse h WHERE h.idHorse = :idHorse", Horse.class)
                .setParameter("idHorse", idHorse)
                .getSingleResult();

        URI uri = URI.create(horse.getImagePath());
        String path = uri.getPath();
        s3Service.deleteFile(path.substring(path.lastIndexOf('/') + 1));
        entityManager.createNativeQuery("DELETE FROM Horse WHERE idHorse = ?")
                .setParameter(1, idHorse)
                .executeUpdate();

    }
}
