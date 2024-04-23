package com.example.usersv.repository;


import com.example.usersv.model.Userdata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserdataRepository extends JpaRepository<Userdata, String> {

    Userdata findUserdataByEmail(String email);

    Userdata findUserdataByResetToken(String token);
}
