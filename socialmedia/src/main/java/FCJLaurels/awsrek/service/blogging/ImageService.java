package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.imageDTO.ImageCreationDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageEditDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ImageService {
    // Upload image to S3 and save metadata
    ImageDTO uploadImageToS3(MultipartFile file) throws IOException;

    // Create a new image
    ImageDTO createImage(ImageCreationDTO imageCreationDTO);

    // Get image by ID
    Optional<ImageDTO> getImageById(String id);

    // Get all images
    List<ImageDTO> getAllImages();

    // Get images by name
    List<ImageDTO> getImagesByName(String name);

    // Get images by type
    List<ImageDTO> getImagesByType(String type);

    // Update image
    Optional<ImageDTO> updateImage(String id, ImageEditDTO imageEditDTO);

    // Delete image
    boolean deleteImage(String id);

    // Delete images by type
    long deleteImagesByType(String type);

    // Search images by name containing
    List<ImageDTO> searchImagesByNameContaining(String nameKeyword);

    // Convert entity to DTO
    ImageDTO convertToDTO(FCJLaurels.awsrek.model.image image);

    // Convert DTO to entity
    FCJLaurels.awsrek.model.image convertToEntity(ImageCreationDTO imageCreationDTO);
}
