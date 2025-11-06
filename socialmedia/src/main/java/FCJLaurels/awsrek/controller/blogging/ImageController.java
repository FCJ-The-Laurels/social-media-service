package FCJLaurels.awsrek.controller.blogging;

import FCJLaurels.awsrek.DTO.imageDTO.ImageCreationDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageDTO;
import FCJLaurels.awsrek.DTO.imageDTO.ImageEditDTO;
import FCJLaurels.awsrek.service.blogging.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
@AllArgsConstructor
@Tag(name = "Image Management", description = "APIs for managing images")
public class ImageController {

    private final ImageService imageService;

    /**
     * Upload/Create a new image
     *
     * Response Codes:
     * - 201 CREATED: Image successfully created, returns the created image with generated ID
     * - 400 BAD REQUEST: Invalid input data (validation errors)
     * - 500 INTERNAL SERVER ERROR: Server error during image creation
     */
    @Operation(summary = "Upload a new image", description = "Creates a new image record with name, URL, and type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image successfully created",
            content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<ImageDTO> createImage(@Valid @RequestBody ImageCreationDTO imageCreationDTO) {
        ImageDTO created = imageService.createImage(imageCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get an image by ID
     *
     * Response Codes:
     * - 200 OK: Image found and returned successfully
     * - 404 NOT FOUND: Image with the specified ID does not exist
     */
    @Operation(summary = "Get image by ID", description = "Retrieves a specific image by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image found",
            content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImageById(
            @Parameter(description = "Image ID", required = true)
            @PathVariable String id) {
        return imageService.getImageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all images
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved all images (empty list if no images exist)
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(summary = "Get all images", description = "Retrieves a list of all images")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all images",
            content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    /**
     * Get images by name
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved images (empty list if no matches found)
     * - 400 BAD REQUEST: Invalid name parameter
     */
    @Operation(summary = "Get images by name", description = "Retrieves all images with a specific name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved images by name",
            content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid name parameter")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<List<ImageDTO>> getImagesByName(
            @Parameter(description = "Image name", required = true)
            @PathVariable String name) {
        return ResponseEntity.ok(imageService.getImagesByName(name));
    }

    /**
     * Get images by type
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved images by type (empty list if no matches found)
     * - 400 BAD REQUEST: Invalid type parameter
     */
    @Operation(summary = "Get images by type", description = "Retrieves all images of a specific type (e.g., jpg, png, gif)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved images by type",
            content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid type parameter")
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ImageDTO>> getImagesByType(
            @Parameter(description = "Image type (e.g., jpg, png, gif)", required = true)
            @PathVariable String type) {
        return ResponseEntity.ok(imageService.getImagesByType(type));
    }

    /**
     * Search images by name keyword
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved matching images (empty list if no matches)
     * - 400 BAD REQUEST: Invalid search query
     */
    @Operation(summary = "Search images by name", description = "Searches for images containing the specified keyword in their name (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching images",
            content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search query")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ImageDTO>> searchImagesByName(
            @Parameter(description = "Name search keyword", required = true)
            @RequestParam String keyword) {
        return ResponseEntity.ok(imageService.searchImagesByNameContaining(keyword));
    }

    /**
     * Update an image
     *
     * Response Codes:
     * - 200 OK: Image successfully updated and returned
     * - 400 BAD REQUEST: Invalid input data (validation errors)
     * - 404 NOT FOUND: Image with the specified ID does not exist
     * - 500 INTERNAL SERVER ERROR: Server error during update
     */
    @Operation(summary = "Update an image", description = "Updates an existing image's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image successfully updated",
            content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ImageDTO> updateImage(
            @Parameter(description = "Image ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody ImageEditDTO imageEditDTO) {
        return imageService.updateImage(id, imageEditDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete an image
     *
     * Response Codes:
     * - 204 NO CONTENT: Image successfully deleted
     * - 404 NOT FOUND: Image with the specified ID does not exist
     * - 500 INTERNAL SERVER ERROR: Server error during deletion
     */
    @Operation(summary = "Delete an image", description = "Deletes an image by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Image successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "Image ID", required = true)
            @PathVariable String id) {
        boolean deleted = imageService.deleteImage(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Delete images by type
     *
     * Response Codes:
     * - 200 OK: Successfully deleted images, returns count of deleted images
     * - 400 BAD REQUEST: Invalid type parameter
     */
    @Operation(summary = "Delete images by type", description = "Deletes all images of a specific type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted images"),
        @ApiResponse(responseCode = "400", description = "Invalid type parameter")
    })
    @DeleteMapping("/type/{type}")
    public ResponseEntity<Long> deleteImagesByType(
            @Parameter(description = "Image type", required = true)
            @PathVariable String type) {
        long count = imageService.deleteImagesByType(type);
        return ResponseEntity.ok(count);
    }
}
