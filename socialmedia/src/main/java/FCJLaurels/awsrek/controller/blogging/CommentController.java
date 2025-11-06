package FCJLaurels.awsrek.controller.blogging;

import FCJLaurels.awsrek.DTO.commentDTO.CommentCreationDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentEditDTO;
import FCJLaurels.awsrek.service.blogging.CommentService;
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
@RequestMapping("/comments")
@AllArgsConstructor
@Tag(name = "Comment Management", description = "APIs for managing blog comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * Create a new comment
     *
     * Response Codes:
     * - 201 CREATED: Comment successfully created, returns the created comment with generated ID
     * - 400 BAD REQUEST: Invalid input data (validation errors)
     * - 500 INTERNAL SERVER ERROR: Server error during comment creation
     */
    @Operation(summary = "Create a new comment", description = "Creates a new comment on a blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment successfully created",
            content = @Content(schema = @Schema(implementation = CommentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentCreationDTO commentCreationDTO) {
        CommentDTO created = commentService.createComment(commentCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get a comment by ID
     *
     * Response Codes:
     * - 200 OK: Comment found and returned successfully
     * - 404 NOT FOUND: Comment with the specified ID does not exist
     */
    @Operation(summary = "Get comment by ID", description = "Retrieves a specific comment by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment found",
            content = @Content(schema = @Schema(implementation = CommentDTO.class))),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable String id) {
        return commentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all comments
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved all comments (empty list if no comments exist)
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(summary = "Get all comments", description = "Retrieves a list of all comments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all comments",
            content = @Content(schema = @Schema(implementation = CommentDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    /**
     * Get comments by blog ID
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved comments for the blog (empty list if no comments found)
     * - 400 BAD REQUEST: Invalid blog ID parameter
     */
    @Operation(summary = "Get comments by blog ID", description = "Retrieves all comments for a specific blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved comments by blog",
            content = @Content(schema = @Schema(implementation = CommentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid blog ID parameter")
    })
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBlogId(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String blogId) {
        return ResponseEntity.ok(commentService.getCommentsByBlogId(blogId));
    }

    /**
     * Get comments by user ID
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved comments by user (empty list if no comments found)
     * - 400 BAD REQUEST: Invalid user ID parameter
     */
    @Operation(summary = "Get comments by user ID", description = "Retrieves all comments created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved comments by user",
            content = @Content(schema = @Schema(implementation = CommentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user ID parameter")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }

    /**
     * Update a comment
     *
     * Response Codes:
     * - 200 OK: Comment successfully updated and returned
     * - 400 BAD REQUEST: Invalid input data (validation errors)
     * - 404 NOT FOUND: Comment with the specified ID does not exist
     * - 500 INTERNAL SERVER ERROR: Server error during update
     */
    @Operation(summary = "Update a comment", description = "Updates an existing comment with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment successfully updated",
            content = @Content(schema = @Schema(implementation = CommentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Comment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody CommentEditDTO commentEditDTO) {
        return commentService.updateComment(id, commentEditDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a comment
     *
     * Response Codes:
     * - 204 NO CONTENT: Comment successfully deleted
     * - 404 NOT FOUND: Comment with the specified ID does not exist
     * - 500 INTERNAL SERVER ERROR: Server error during deletion
     */
    @Operation(summary = "Delete a comment", description = "Deletes a comment by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comment successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Comment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable String id) {
        boolean deleted = commentService.deleteComment(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Delete all comments by blog ID
     *
     * Response Codes:
     * - 200 OK: Successfully deleted comments, returns count of deleted comments
     * - 400 BAD REQUEST: Invalid blog ID parameter
     */
    @Operation(summary = "Delete comments by blog ID", description = "Deletes all comments associated with a specific blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted comments"),
        @ApiResponse(responseCode = "400", description = "Invalid blog ID parameter")
    })
    @DeleteMapping("/blog/{blogId}")
    public ResponseEntity<Long> deleteCommentsByBlogId(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String blogId) {
        long count = commentService.deleteCommentsByBlogId(blogId);
        return ResponseEntity.ok(count);
    }

    /**
     * Count comments by blog ID
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved count
     */
    @Operation(summary = "Count comments by blog", description = "Returns the total number of comments for a blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved comment count")
    })
    @GetMapping("/blog/{blogId}/count")
    public ResponseEntity<Long> countCommentsByBlogId(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String blogId) {
        long count = commentService.countCommentsByBlogId(blogId);
        return ResponseEntity.ok(count);
    }
}
