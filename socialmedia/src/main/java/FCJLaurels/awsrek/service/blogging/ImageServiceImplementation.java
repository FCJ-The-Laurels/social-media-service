package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.imageDTO.ImageCreationDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageEditDTO;
import FCJLaurels.awsrek.model.image;
import FCJLaurels.awsrek.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ImageServiceImplementation implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Mono<ImageDTO> createImage(ImageCreationDTO imageCreationDTO) {
        image newImage = image.builder()
                .name(imageCreationDTO.getName())
                .url(imageCreationDTO.getUrl())
                .type(imageCreationDTO.getType())
                .build();

        return imageRepository.save(newImage)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<ImageDTO> getImageById(String id) {
        return imageRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public Flux<ImageDTO> getAllImages() {
        return imageRepository.findAll()
                .map(this::mapToDTO);
    }

    @Override
    public Flux<ImageDTO> getImagesByName(String name) {
        return imageRepository.findByName(name)
                .map(this::mapToDTO);
    }

    @Override
    public Flux<ImageDTO> getImagesByType(String type) {
        return imageRepository.findByType(type)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<ImageDTO> updateImage(String id, ImageEditDTO imageEditDTO) {
        return imageRepository.findById(id)
                .flatMap(img -> {
                    img.setName(imageEditDTO.getName());
                    img.setUrl(imageEditDTO.getUrl());
                    img.setType(imageEditDTO.getType());
                    return imageRepository.save(img);
                })
                .map(this::mapToDTO);
    }

    @Override
    public Mono<Boolean> deleteImage(String id) {
        return imageRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return imageRepository.deleteById(id)
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<Long> deleteImagesByType(String type) {
        return imageRepository.findByType(type)
                .flatMap(img -> imageRepository.deleteById(img.getId()).thenReturn(1L))
                .reduce(0L, Long::sum);
    }

    @Override
    public Flux<ImageDTO> searchImagesByNameContaining(String nameKeyword) {
        return imageRepository.findAll()
                .filter(img -> img.getName() != null &&
                               img.getName().toLowerCase().contains(nameKeyword.toLowerCase()))
                .map(this::mapToDTO);
    }

    @Override
    public ImageDTO convertToDTO(image image) {
        return mapToDTO(image);
    }

    @Override
    public image convertToEntity(ImageCreationDTO imageCreationDTO) {
        return image.builder()
                .name(imageCreationDTO.getName())
                .url(imageCreationDTO.getUrl())
                .type(imageCreationDTO.getType())
                .build();
    }

    private ImageDTO mapToDTO(image entity) {
        if (entity == null) return null;
        return ImageDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .url(entity.getUrl())
                .type(entity.getType())
                .creationDate(entity.getCreationDate())
                .build();
    }
}
