//package FCJLaurels.awsrek.service.aws;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class S3Service {
//
//    private final S3Client s3Client;
//
//    @Value("${aws.s3.bucket-name}")
//    private String bucketName;
//
//    @Value("${aws.s3.region}")
//    private String region;
//
//    /**
//     * Upload a file to S3 and return its public URL
//     *
//     * @param file The multipart file to upload
//     * @return The public URL of the uploaded file
//     * @throws IOException if file processing fails
//     */
//    public String uploadFile(MultipartFile file) throws IOException {
//        // Generate unique file name to avoid collisions
//        String originalFilename = file.getOriginalFilename();
//        String extension = originalFilename != null && originalFilename.contains(".")
//            ? originalFilename.substring(originalFilename.lastIndexOf("."))
//            : "";
//        String fileName = UUID.randomUUID().toString() + extension;
//
//        // Get content type
//        String contentType = file.getContentType();
//        if (contentType == null || contentType.isEmpty()) {
//            contentType = "application/octet-stream";
//        }
//
//        try {
//            // Upload file to S3
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//                    .contentType(contentType)
//                    .acl(ObjectCannedACL.PUBLIC_READ) // Make the file publicly readable
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//
//            // Return the public URL
//            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
//                bucketName, region, fileName);
//
//            log.info("File uploaded successfully to S3: {}", fileUrl);
//            return fileUrl;
//
//        } catch (S3Exception e) {
//            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * Delete a file from S3
//     *
//     * @param fileUrl The URL of the file to delete
//     * @return true if deletion was successful, false otherwise
//     */
//    public boolean deleteFile(String fileUrl) {
//        try {
//            // Extract the file key from the URL
//            String fileName = extractFileNameFromUrl(fileUrl);
//
//            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//                    .build();
//
//            s3Client.deleteObject(deleteObjectRequest);
//            log.info("File deleted successfully from S3: {}", fileName);
//            return true;
//
//        } catch (S3Exception e) {
//            log.error("Failed to delete file from S3: {}", e.getMessage(), e);
//            return false;
//        }
//    }
//
//    /**
//     * Extract file name from S3 URL
//     */
//    private String extractFileNameFromUrl(String fileUrl) {
//        // URL format: https://bucket-name.s3.region.amazonaws.com/filename
//        String[] parts = fileUrl.split("/");
//        return parts[parts.length - 1];
//    }
//
//    /**
//     * Check if file exists in S3
//     */
//    public boolean fileExists(String fileName) {
//        try {
//            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//                    .build();
//
//            s3Client.headObject(headObjectRequest);
//            return true;
//
//        } catch (NoSuchKeyException e) {
//            return false;
//        } catch (S3Exception e) {
//            log.error("Error checking file existence: {}", e.getMessage(), e);
//            return false;
//        }
//    }
//}
//
