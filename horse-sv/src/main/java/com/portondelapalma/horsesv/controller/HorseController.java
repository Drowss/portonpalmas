package com.portondelapalma.horsesv.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portondelapalma.horsesv.dto.HorseDto;
import com.portondelapalma.horsesv.model.Horse;
import com.portondelapalma.horsesv.service.IHorseService;
import com.portondelapalma.horsesv.service.S3Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("/v1")
public class HorseController {

    @Autowired
    private IHorseService iHorseService;

    @Autowired
    private S3Service s3Service;

    @GetMapping()
    private List<HorseDto> getAllHorses() { //Endpoint para todos los roles
        return iHorseService.getAllHorses();
    }

    @GetMapping("/search") //Endpoint para todos los roles
    public List<HorseDto> getHorsesByBreed(@RequestParam String breed) {
        return iHorseService.getAllByBreed(breed);
    }

    @PostMapping("/upload") //Endpoint para admin
    public Horse upload(@RequestPart("file") MultipartFile file,@Valid @RequestPart("horse") String horseJson) throws JsonProcessingException {
        return iHorseService.createHorse(file, horseJson);
    }

    @DeleteMapping("/delete/{idHorse}") //Endpoint para admin
    public ResponseEntity<String> deleteFile(@PathVariable("idHorse") Long idHorse) throws URISyntaxException, MalformedURLException {
        return iHorseService.deleteHorse(idHorse);
    }

    @PutMapping(value = "/put/{idHorse}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //Endpoint para admin
    public Horse putHorse(@PathVariable("idHorse") Long idHorse, @RequestPart(value = "file", required = false) MultipartFile file,
                          @RequestPart(value = "horse", required = false) String horseJson) throws JsonProcessingException, URISyntaxException {
        return iHorseService.putHorse(idHorse, file, horseJson);
    }
}
