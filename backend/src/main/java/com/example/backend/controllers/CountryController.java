package com.example.backend.controllers;

import com.example.backend.models.Artist;
import com.example.backend.models.Painting;
import com.example.backend.repositories.CountryRepository;
import com.example.backend.tools.DataValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.models.Country;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class CountryController {
    @Autowired
    CountryRepository countryRepository;
    @GetMapping("/countries")
    public List getAllCountries() {
        return countryRepository.findAll();
        }

    @PostMapping("/countries")
    public ResponseEntity<Object> createCountry(@RequestBody Country country)
        throws DataValidationException {
            try {
                Country nc = countryRepository.save(country);
                return new ResponseEntity<Object>(nc, HttpStatus.OK);
            }
            catch(Exception ex) {
                //String error;
                if (ex.getMessage().contains("countries.name_UNIQUE"))
                    throw new DataValidationException("countryalreadyexists");
                else if (ex.getMessage().contains("not-null")) {
                    throw new DataValidationException("countrycantbenull");
                }
                else
                {
                    throw new DataValidationException("undefinederror");
                }
                // Map<String, String> map = new HashMap<>();
                //map.put("error", error);
                //return ResponseEntity.ok(map);
            }
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<Country> getCountry(@PathVariable(value = "id") Long countryId)
            throws DataValidationException {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(()->new DataValidationException("Страна с таким индексом не найдена"));
        return ResponseEntity.ok(country);
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable(value = "id") Long countryId,
@Valid @RequestBody Country countryDetails) throws DataValidationException{
        try{
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new DataValidationException("Страна с таким индексом не найдена"));
            country.name = countryDetails.name;
            countryRepository.save(country);
            return ResponseEntity.ok(country);
        }
        catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("countries.name_UNIQUE"))
                throw new DataValidationException("Эта страна уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PostMapping("/deletecountries")
    public ResponseEntity deleteCountries(@Valid @RequestBody List<Country> countries) throws DataValidationException {
        try{
        countryRepository.deleteAll(countries);
        return new ResponseEntity(HttpStatus.OK);}
        catch (Exception e)
        {
            throw new DataValidationException(e.getMessage());
        }
    }


    @GetMapping("/countries/{id}/artists")
    public ResponseEntity<List<Artist>> getCountryArtists(@PathVariable(value = "id") Long countryId){
            Optional<Country> cc = countryRepository.findById(countryId);
            if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().artists);
            }
            return ResponseEntity.ok(new ArrayList<Artist>());
    }


}
