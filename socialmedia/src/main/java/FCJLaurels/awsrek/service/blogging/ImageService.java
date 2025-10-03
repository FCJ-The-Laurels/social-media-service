package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.imageDTO.ImageCreationDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageEditDTO;
import FCJLaurels.awsrek.model.image;

import java.util.List;
import java.util.Optional;

public interface ImageService {
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
    ImageDTO convertToDTO(image image);

    // Convert DTO to entity
    image convertToEntity(ImageCreationDTO imageCreationDTO);
}
