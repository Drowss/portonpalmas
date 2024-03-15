package com.portondelapalma.horsesv.repository;

import com.portondelapalma.horsesv.model.Horse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHorseRepository extends JpaRepository<Horse, Long> {

    @Query("select h from Horse h where h.breed = ?1")
    List<Horse> getAllByBreed(String breed);
}
