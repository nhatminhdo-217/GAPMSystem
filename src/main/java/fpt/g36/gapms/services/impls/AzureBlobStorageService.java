package fpt.g36.gapms.services.impls;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureBlobStorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    private BlobContainerClient getBlobContainerClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        return blobServiceClient.getBlobContainerClient(containerName);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        BlobClient blobClient = getBlobContainerClient().getBlobClient(fileName);

        try(InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);
        }

        return blobClient.getBlobUrl();
    }

    public String uploadFile(byte[] data, String fileName) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        BlobClient blobClient = getBlobContainerClient().getBlobClient(uniqueFileName);

        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            blobClient.upload(inputStream, data.length, true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Azure Blob Storage", e);
        }

        return blobClient.getBlobUrl();
    }
}
