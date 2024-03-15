package com.portondelapalma.productsv.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    String saveFile(MultipartFile file);
    String deleteFile(String filename);
    List<String> listAllFiles();
}
