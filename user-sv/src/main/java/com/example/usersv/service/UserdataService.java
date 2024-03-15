package com.example.usersv.service;

import com.example.usersv.model.Userdata;
import com.example.usersv.repository.IUserdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserdataService {

    @Autowired
    private IUserdataRepository iUserdataRepository;

    public void saveUserdata(Userdata userdata) {
        iUserdataRepository.save(userdata);
    }

    public Userdata getUserdata(String email) {
        return iUserdataRepository.findUserdataByEmail(email);
    }

    public void deleteUserdata(String email) {
        iUserdataRepository.deleteById(email);
    }

    public void updateUserdata(Userdata userdata) {

    }

    public List<Userdata> getAllUserdata() {
        return iUserdataRepository.findAll();
    }
}
