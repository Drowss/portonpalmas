package com.portondelapalma.horsesv.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    String saveFile(MultipartFile file);
    byte[] downloadFile(String filename);
    String deleteFile(String filename);

    List<String> listAllFiles();
}
