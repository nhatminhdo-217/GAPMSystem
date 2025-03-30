package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.services.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public String saveImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("URL ảnh không hợp lệ");
        }

        // Tạo tên file duy nhất
        String fileNameUnique = UUID.randomUUID().toString() + ".jpg";

        Path pathDir = Paths.get("uploads");
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }

        Path destination = Paths.get(pathDir.toString(), fileNameUnique);

        // Tải ảnh từ URL
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileNameUnique;
    }


    @Override
    public String saveImageMultiFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File áº£nh khÃ´ng há»£p lá»‡");
        }

        String fileNameUnique = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

        Path pathDir = Paths.get("uploads");
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }

        Path destination = pathDir.resolve(fileNameUnique);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileNameUnique;
    }


    // truyền nhiều ảnh vô
    public List<String> saveListImageMultiFile(MultipartFile[] files) throws IOException {
        List<String> fileNames = new ArrayList<>();

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Danh sách file ảnh không hợp lệ");
        }

        Path pathDir = Paths.get("uploads");
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String fileNameUnique = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
                Path destination = pathDir.resolve(fileNameUnique);

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                fileNames.add(fileNameUnique);
            }
        }

        return fileNames;
    }

}
