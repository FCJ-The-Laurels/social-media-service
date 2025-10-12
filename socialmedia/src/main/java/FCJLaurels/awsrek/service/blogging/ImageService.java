package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.imageDTO.ImageCreationDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageEditDTO;
import FCJLaurels.awsrek.model.image;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageService {
    // Create a new image
    Mono<ImageDTO> createImage(ImageCreationDTO imageCreationDTO);

    // Get image by ID
    Mono<ImageDTO> getImageById(String id);

    // Get all images
    Flux<ImageDTO> getAllImages();

    // Get images by name
    Flux<ImageDTO> getImagesByName(String name);

    // Get images by type
    Flux<ImageDTO> getImagesByType(String type);

    // Update image
    Mono<ImageDTO> updateImage(String id, ImageEditDTO imageEditDTO);

    // Delete image
    Mono<Boolean> deleteImage(String id);

    // Delete images by type
    Mono<Long> deleteImagesByType(String type);

    // Search images by name containing
    Flux<ImageDTO> searchImagesByNameContaining(String nameKeyword);

    // Convert entity to DTO
    ImageDTO convertToDTO(image image);

    // Convert DTO to entity
    image convertToEntity(ImageCreationDTO imageCreationDTO);
}
