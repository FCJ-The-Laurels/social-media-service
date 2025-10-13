package FCJLaurels.awsrek.controller.blogging;

import FCJLaurels.awsrek.DTO.likeDTO.LikeCreationDTO;
import FCJLaurels.awsrek.DTO.likeDTO.LikeDTO;
import FCJLaurels.awsrek.service.blogging.LikeService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/likes")
@AllArgsConstructor
@Tag(name = "Like Management", description = "APIs for managing blog likes")
public class LikeController {

    private final LikeService likeService;

    /**
     * Create a new like (user likes a blog)
     *
     * Response Codes:
     * - 201 CREATED: Like successfully created, returns the created like with generated ID
     * - 400 BAD REQUEST: Invalid input data (validation errors)
     * - 500 INTERNAL SERVER ERROR: Server error during like creation
     */
    @Operation(summary = "Create a new like", description = "Creates a new like when a user likes a blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Like successfully created",
            content = @Content(schema = @Schema(implementation = LikeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public Mono<ResponseEntity<LikeDTO>> createLike(@Valid @RequestBody LikeCreationDTO likeCreationDTO) {
        return likeService.createLike(likeCreationDTO)
                .map(like -> ResponseEntity.status(HttpStatus.CREATED).body(like));
    }

    /**
     * Get a like by ID
     *
     * Response Codes:
     * - 200 OK: Like found and returned successfully
     * - 404 NOT FOUND: Like with the specified ID does not exist
     */
    @Operation(summary = "Get like by ID", description = "Retrieves a specific like by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Like found",
            content = @Content(schema = @Schema(implementation = LikeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Like not found")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<LikeDTO>> getLikeById(
            @Parameter(description = "Like ID", required = true)
            @PathVariable String id) {
        return likeService.getLikeById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get all likes
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved all likes (empty list if no likes exist)
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(summary = "Get all likes", description = "Retrieves a list of all likes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all likes",
            content = @Content(schema = @Schema(implementation = LikeDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Flux<LikeDTO> getAllLikes() {
        return likeService.getAllLikes();
    }

    /**
     * Get likes by blog ID
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved likes for the blog (empty list if no likes found)
     * - 400 BAD REQUEST: Invalid blog ID parameter
     */
    @Operation(summary = "Get likes by blog ID", description = "Retrieves all likes for a specific blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved likes by blog",
            content = @Content(schema = @Schema(implementation = LikeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid blog ID parameter")
    })
    @GetMapping("/blog/{blogId}")
    public Flux<LikeDTO> getLikesByBlogId(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String blogId) {
        return likeService.getLikesByBlogId(blogId);
    }

    /**
     * Get likes by user ID
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved likes by user (empty list if no likes found)
     * - 400 BAD REQUEST: Invalid user ID parameter
     */
    @Operation(summary = "Get likes by user ID", description = "Retrieves all likes created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved likes by user",
            content = @Content(schema = @Schema(implementation = LikeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user ID parameter")
    })
    @GetMapping("/user/{userId}")
    public Flux<LikeDTO> getLikesByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        return likeService.getLikesByUserId(userId);
    }

    /**
     * Check if user has liked a blog
     *
     * Response Codes:
     * - 200 OK: Successfully checked, returns boolean result
     * - 400 BAD REQUEST: Invalid parameters
     */
    @Operation(summary = "Check if user liked blog", description = "Checks whether a user has liked a specific blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully checked like status"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/check")
    public Mono<ResponseEntity<Boolean>> hasUserLikedBlog(
            @Parameter(description = "User ID", required = true)
            @RequestParam String userId,
            @Parameter(description = "Blog ID", required = true)
            @RequestParam String blogId) {
        return likeService.hasUserLikedBlog(userId, blogId)
                .map(ResponseEntity::ok);
    }

    /**
     * Get specific like by user and blog
     *
     * Response Codes:
     * - 200 OK: Like found and returned
     * - 404 NOT FOUND: No like found for this user and blog combination
     */
    @Operation(summary = "Get like by user and blog", description = "Retrieves a specific like by user ID and blog ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Like found",
            content = @Content(schema = @Schema(implementation = LikeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Like not found")
    })
    @GetMapping("/user/{userId}/blog/{blogId}")
    public Mono<ResponseEntity<LikeDTO>> getLikeByUserAndBlog(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String blogId) {
        return likeService.getLikeByUserIdAndBlogId(userId, blogId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Toggle like (like/unlike)
     *
     * Response Codes:
     * - 200 OK: Like toggled successfully, returns created like if liked
     * - 204 NO CONTENT: Like removed successfully (unliked)
     * - 400 BAD REQUEST: Invalid parameters
     */
    @Operation(summary = "Toggle like", description = "Likes the blog if not liked, unlikes if already liked")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blog liked successfully",
            content = @Content(schema = @Schema(implementation = LikeDTO.class))),
        @ApiResponse(responseCode = "204", description = "Blog unliked successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @PostMapping("/toggle")
    public Mono<ResponseEntity<LikeDTO>> toggleLike(
            @Parameter(description = "User ID", required = true)
            @RequestParam String userId,
            @Parameter(description = "Blog ID", required = true)
            @RequestParam String blogId) {
        return likeService.toggleLike(userId, blogId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    /**
     * Delete a like by ID
     *
     * Response Codes:
     * - 204 NO CONTENT: Like successfully deleted
     * - 404 NOT FOUND: Like with the specified ID does not exist
     * - 500 INTERNAL SERVER ERROR: Server error during deletion
     */
    @Operation(summary = "Delete a like", description = "Deletes a like by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Like successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Like not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLike(
            @Parameter(description = "Like ID", required = true)
            @PathVariable String id) {
        return likeService.deleteLike(id)
                .map(deleted -> deleted ?
                    ResponseEntity.noContent().<Void>build() :
                    ResponseEntity.notFound().<Void>build());
    }

    /**
     * Delete like by user and blog
     *
     * Response Codes:
     * - 204 NO CONTENT: Like successfully deleted
     * - 404 NOT FOUND: No like found for this user and blog combination
     */
    @Operation(summary = "Delete like by user and blog", description = "Deletes a like by user ID and blog ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Like successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Like not found")
    })
    @DeleteMapping("/user/{userId}/blog/{blogId}")
    public Mono<ResponseEntity<Void>> deleteLikeByUserAndBlog(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String blogId) {
        return likeService.deleteLikeByUserIdAndBlogId(userId, blogId)
                .map(deleted -> deleted ?
                    ResponseEntity.noContent().<Void>build() :
                    ResponseEntity.notFound().<Void>build());
    }

    /**
     * Count likes by blog ID
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved count
     */
    @Operation(summary = "Count likes by blog", description = "Returns the total number of likes for a blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved like count")
    })
    @GetMapping("/blog/{blogId}/count")
    public Mono<ResponseEntity<Long>> countLikesByBlogId(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String blogId) {
        return likeService.countLikesByBlogId(blogId)
                .map(ResponseEntity::ok);
    }

    /**
     * Count likes by user ID
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved count
     */
    @Operation(summary = "Count likes by user", description = "Returns the total number of likes created by a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved like count")
    })
    @GetMapping("/user/{userId}/count")
    public Mono<ResponseEntity<Long>> countLikesByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        return likeService.countLikesByUserId(userId)
                .map(ResponseEntity::ok);
    }
}
