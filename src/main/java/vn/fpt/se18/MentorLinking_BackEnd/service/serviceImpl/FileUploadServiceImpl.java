package vn.fpt.se18.MentorLinking_BackEnd.service.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class FileUploadServiceImpl {

    private static final Logger logger = Logger.getLogger(FileUploadServiceImpl.class.getName());

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "pdf", "doc", "docx"};

    private Cloudinary getCloudinaryInstance() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadFile(MultipartFile file, String folder) {
        try {
            validateFile(file);

            Cloudinary cloudinary = getCloudinaryInstance();

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "mentor-linking/" + folder,
                            "resource_type", "auto",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            String fileUrl = (String) uploadResult.get("secure_url");
            logger.info("File uploaded successfully to Cloudinary: " + fileUrl);

            return fileUrl;

        } catch (IOException e) {
            logger.severe("Error uploading file to Cloudinary: " + e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 10MB");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException(
                    "File type not allowed. Allowed types: jpg, jpeg, png, pdf, doc, docx"
            );
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public boolean deleteFile(String fileUrl, String folder) {
        try {
            Cloudinary cloudinary = getCloudinaryInstance();

            // Extract public_id từ URL Cloudinary
            String publicId = extractPublicId(fileUrl, folder);

            Map deleteResult = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "auto")
            );

            String result = (String) deleteResult.get("result");
            if ("ok".equals(result)) {
                logger.info("File deleted successfully from Cloudinary: " + publicId);
                return true;
            } else {
                logger.warning("Failed to delete file from Cloudinary: " + publicId);
                return false;
            }

        } catch (IOException e) {
            logger.severe("Error deleting file from Cloudinary: " + e.getMessage());
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    private String extractPublicId(String fileUrl, String folder) {
        // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/image/upload/{public_id}
        String[] parts = fileUrl.split("/upload/");
        if (parts.length == 2) {
            // Remove file extension
            String path = parts[1];
            return path.substring(0, path.lastIndexOf("."));
        }
        throw new IllegalArgumentException("Invalid Cloudinary URL format");
    }
}
