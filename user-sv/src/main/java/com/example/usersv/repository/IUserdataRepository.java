package com.example.usersv.repository;


import com.example.usersv.model.Userdata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserdataRepository extends JpaRepository<Userdata, String> {

    Userdata findUserdataByEmail(String email);

    Userdata findUserdataByResetToken(String token);

    @Query("SELECT u FROM Userdata u WHERE u.email = :email")
    Userdata findUserdataByEmailORM(@Param("email") String email);

    @Query("SELECT u FROM Userdata u WHERE u.resetToken = :token")
    Userdata findUserdataByResetTokenORM(@Param("token") String token);

    @Modifying
    @Query(value = "INSERT INTO Userdata (email, name, password, address, cellphone, dni, role, idCart)" +
            " VALUES (:email, :name, :password, :address, :cellphone, :dni, :role, :idCart)",
            nativeQuery = true)
    void saveUserdataORM(@Param("email") String email, @Param("name") String name, @Param("password") String password,
                      @Param("address") String address, @Param("cellphone") String cellphone, @Param("dni") String dni,
                      @Param("role") String role, @Param("idCart") Long idCart);

    @Modifying
    @Query(value = "UPDATE Userdata SET name = :name, password = :password, address = :address, cellphone = :cellphone, dni = :dni, role = :role, idCart = :idCart WHERE email = :email", nativeQuery = true)
    void updateUserdataORM(@Param("email") String email, @Param("name") String name, @Param("password") String password,
                        @Param("address") String address, @Param("cellphone") String cellphone, @Param("dni") String dni,
                        @Param("role") String role, @Param("idCart") Long idCart);

    @Query(value = "DELETE FROM Userdata u WHERE u.email = :email", nativeQuery = true)
    void deleteUserdataORM(@Param("email") String email);

    @Query(value = "SELECT * FROM Userdata", nativeQuery = true)
    List<Userdata> findAllUserdataORM();
}
