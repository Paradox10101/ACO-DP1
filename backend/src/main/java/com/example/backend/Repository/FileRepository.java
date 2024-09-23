package com.example.backend.Repository;

import org.springframework.web.multipart.MultipartFile;


public interface FileRepository {
    public void saveFile(MultipartFile file);
}
