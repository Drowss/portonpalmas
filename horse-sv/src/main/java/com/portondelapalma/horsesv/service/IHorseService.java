package com.portondelapalma.horsesv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portondelapalma.horsesv.dto.HorseDto;
import com.portondelapalma.horsesv.model.Horse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

public interface IHorseService {
    Horse createHorse(MultipartFile multipartFile, String horseJson) throws JsonProcessingException;
    ResponseEntity<String> deleteHorse(Long idHorse) throws MalformedURLException, URISyntaxException;
    List<HorseDto> getAllHorses();
    List<HorseDto> getAllByBreed(String breed);
    Horse putHorse(Long id, MultipartFile multipartFile, String horseJson) throws URISyntaxException, JsonProcessingException;

    Horse findHorseByIdHorseQuery(Long idHorse);
    List<Horse> findAllHorsesSQL();
    void saveHorseSQL(MultipartFile file,String horseJson) throws JsonProcessingException;
    void updateHorseSQL(Long idHorse, MultipartFile file, String horseJson) throws JsonProcessingException, URISyntaxException;
    void deleteHorseSQL(Long idHorse);
}
