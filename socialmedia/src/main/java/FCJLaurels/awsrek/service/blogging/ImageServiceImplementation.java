package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.imageDTO.ImageCreationDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageEditDTO;
import FCJLaurels.awsrek.model.image;
import FCJLaurels.awsrek.repository.ImageRepository;
import FCJLaurels.awsrek.service.aws.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImageServiceImplementation implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    public ImageDTO uploadImageToS3(MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Get file metadata
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        // Extract file extension/type
        String fileType = "unknown";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        } else if (contentType != null && contentType.contains("/")) {
            fileType = contentType.substring(contentType.lastIndexOf("/") + 1);
        }

        log.info("Uploading file to S3: {} (type: {})", originalFilename, fileType);

        // Upload to S3
        String fileUrl = s3Service.uploadFile(file);

        // Save image metadata to database
        image newImage = image.builder()
                .name(originalFilename)
                .url(fileUrl)
                .type(fileType)
                .build();

        image saved = imageRepository.save(newImage);
        log.info("Image metadata saved to database with ID: {}", saved.getId());

        return mapToDTO(saved);
    }

    @Override
    public ImageDTO createImage(ImageCreationDTO imageCreationDTO) {
        image newImage = image.builder()
                .name(imageCreationDTO.getName())
                .url(imageCreationDTO.getUrl())
                .type(imageCreationDTO.getType())
                .build();

        image saved = imageRepository.save(newImage);
        return mapToDTO(saved);
    }

    @Override
    public Optional<ImageDTO> getImageById(String id) {
        return imageRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public List<ImageDTO> getAllImages() {
        return imageRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ImageDTO> getImagesByName(String name) {
        return imageRepository.findByName(name).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ImageDTO> getImagesByType(String type) {
        return imageRepository.findByType(type).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<ImageDTO> updateImage(String id, ImageEditDTO imageEditDTO) {
        Optional<image> existing = imageRepository.findById(id);
        if (existing.isPresent()) {
            image img = existing.get();
            img.setName(imageEditDTO.getName());
            img.setUrl(imageEditDTO.getUrl());
            img.setType(imageEditDTO.getType());
            image saved = imageRepository.save(img);
            return Optional.of(mapToDTO(saved));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteImage(String id) {
        boolean exists = imageRepository.existsById(id);
        if (exists) {
            imageRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public long deleteImagesByType(String type) {
        List<image> imgs = imageRepository.findByType(type);
        long deleted = 0;
        for (image img : imgs) {
            imageRepository.deleteById(img.getId());
            deleted++;
        }
        return deleted;
    }

    @Override
    public List<ImageDTO> searchImagesByNameContaining(String nameKeyword) {
        return imageRepository.findAll().stream()
                .filter(img -> img.getName() != null && img.getName().toLowerCase().contains(nameKeyword.toLowerCase()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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
