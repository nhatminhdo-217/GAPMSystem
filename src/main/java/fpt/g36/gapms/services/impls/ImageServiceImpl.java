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
}
