package fpt.g36.gapms.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

     String saveImage(String imageUrl) throws IOException;

    String saveImageMultiFile(MultipartFile file) throws IOException;
}
