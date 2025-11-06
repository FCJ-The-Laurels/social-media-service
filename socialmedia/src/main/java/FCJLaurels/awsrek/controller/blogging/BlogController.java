package FCJLaurels.awsrek.controller.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogPageResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogCursorResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDisplay;
import FCJLaurels.awsrek.DTO.blogDTO.CursorPageDTO;
import FCJLaurels.awsrek.service.blogging.BlogService;
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
@RequestMapping("/blogs")
@AllArgsConstructor
@Tag(name = "Blog Management", description = "APIs for managing blog posts")
public class BlogController {

    private final BlogService blogService;

    /**
     * Create a new blog post
     *
     * Response Codes:
     * - 201 CREATED: Blog successfully created, returns the created blog with generated ID
     * - 400 BAD REQUEST: Invalid input data (validation errors)
     * - 401 UNAUTHORIZED: Missing or invalid user ID in header
     * - 500 INTERNAL SERVER ERROR: Server error during blog creation
     */
    @Operation(summary = "Create a new blog post", description = "Creates a new blog post with the provided information. User ID is extracted from X-User-Id header.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Blog successfully created",
            content = @Content(schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid user ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<BlogDTO> createBlog(
            @Valid @RequestBody BlogCreationDTO blogCreationDTO,
            @Parameter(description = "User ID from API Gateway", required = true)
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BlogDTO created = blogService.createBlog(blogCreationDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get a blog post by ID
     *
     * Response Codes:
     * - 200 OK: Blog found and returned successfully
     * - 404 NOT FOUND: Blog with the specified ID does not exist
     */
    @Operation(summary = "Get blog by ID", description = "Retrieves a specific blog post by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blog found",
            content = @Content(schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    @GetMapping("/{id}/search-by-id")
    public ResponseEntity<BlogDTO> getBlogById(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all blog posts
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved all blogs (empty list if no blogs exist)
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(summary = "Get all blogs", description = "Retrieves a list of all blog posts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all blogs",
            content = @Content(schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "403", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping
    public ResponseEntity<List<BlogDTO>> getAllBlogs() {
        List<BlogDTO> list = blogService.getAllBlogs();
        return ResponseEntity.ok(list);
    }

    /**
     * Get blogs by author
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved blogs by author (empty list if no blogs found)
     * - 400 BAD REQUEST: Invalid author parameter
     */
    @Operation(summary = "Get blogs by author", description = "Retrieves all blog posts written by a specific author")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved blogs by author",
            content = @Content(schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid author parameter")
    })
    @GetMapping("/author/{author}")
    public ResponseEntity<List<BlogDTO>> getBlogsByAuthor(
            @Parameter(description = "Author name", required = true)
            @PathVariable String author) {
        return ResponseEntity.ok(blogService.getBlogsByAuthor(author));
    }

    /**
     * Search blogs by title
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved matching blogs (empty list if no matches)
     * - 400 BAD REQUEST: Invalid search query
     */
    @Operation(summary = "Search blogs by title", description = "Searches for blog posts containing the specified title keyword (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching blogs",
            content = @Content(schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search query")
    })
    @GetMapping("/search-by-title")
    public ResponseEntity<List<BlogDTO>> searchBlogsByTitle(
            @Parameter(description = "Title search keyword", required = true)
            @RequestParam String title) {
        return ResponseEntity.ok(blogService.searchBlogsByTitle(title));
    }

    /**
     * Update a blog post
     *
     * Response Codes:
     * - 200 OK: Blog successfully updated and returned
     * - 400 BAD REQUEST: Invalid input data (validation errors)
     * - 404 NOT FOUND: Blog with the specified ID does not exist
     * - 500 INTERNAL SERVER ERROR: Server error during update
     */
    @Operation(summary = "Update a blog post", description = "Updates an existing blog post with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blog successfully updated",
            content = @Content(schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Blog not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/update-blog")
    public ResponseEntity<BlogDTO> updateBlog(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody BlogEditDTO blogEditDTO) {
        return blogService.updateBlog(id, blogEditDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a blog post
     *
     * Response Codes:
     * - 204 NO CONTENT: Blog successfully deleted (no response body)
     * - 404 NOT FOUND: Blog with the specified ID does not exist
     * - 500 INTERNAL SERVER ERROR: Server error during deletion
     */
    @Operation(summary = "Delete a blog post", description = "Deletes a blog post by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Blog successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Blog not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String id) {
        boolean deleted = blogService.deleteBlog(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Get paginated blogs (Traditional offset-based pagination)
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved paginated blogs
     * - 400 BAD REQUEST: Invalid pagination parameters
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(
        summary = "Get paginated blogs",
        description = "Retrieves blogs with traditional offset-based pagination. Useful for page numbers display."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated blogs",
            content = @Content(schema = @Schema(implementation = BlogPageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paginated")
    public ResponseEntity<BlogPageResponse> getPaginatedBlogs(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getPaginatedBlogs(page, size));
    }

    /**
     * Get blogs using cursor-based pagination (Infinite scroll like Facebook/Amazon)
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved blogs with cursor
     * - 400 BAD REQUEST: Invalid cursor or size parameter
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(
        summary = "Get blogs with cursor pagination (Infinite Scroll)",
        description = "Retrieves blogs using cursor-based pagination for infinite scrolling. " +
                      "Perfect for continuous loading like Facebook feed or Amazon product list. " +
                      "Pass the 'nextCursor' from previous response to get next batch of results."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved blogs",
            content = @Content(schema = @Schema(implementation = BlogCursorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid cursor or size parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/feed")
    public ResponseEntity<BlogCursorResponse> getBlogsFeed(@RequestParam(required = false) String cursor,
                                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getBlogsByCursor(cursor, size));
    }

    /**
     * Get newest blogs with cursor pagination using BlogDisplay DTO (Optimized for social media feeds)
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved newest blogs with display information
     * - 400 BAD REQUEST: Invalid cursor or size parameter
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(
        summary = "Get newest blogs with cursor pagination (Display Format)",
        description = "Retrieves the newest blog posts using cursor-based pagination with BlogDisplay format. " +
                      "This endpoint is optimized for social media feeds with author information. " +
                      "Pass the 'nextCursor' from previous response to load more blogs."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved newest blogs",
            content = @Content(schema = @Schema(implementation = CursorPageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid cursor or size parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/newest/cursor")
    public ResponseEntity<CursorPageDTO<BlogDisplay>> getNewestBlogsWithCursor(
            @Parameter(description = "Cursor for pagination (Base64 encoded timestamp)", required = false)
            @RequestParam(required = false) String cursor,
            @Parameter(description = "Number of blogs to retrieve", required = false)
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getNewestBlogsWithCursor(cursor, size));
    }

    /**
     * Get newest blogs with offset pagination using BlogDisplay DTO
     *
     * Response Codes:
     * - 200 OK: Successfully retrieved newest blogs with pagination info
     * - 400 BAD REQUEST: Invalid pagination parameters
     * - 500 INTERNAL SERVER ERROR: Server error during retrieval
     */
    @Operation(
        summary = "Get newest blogs with offset pagination (Display Format)",
        description = "Retrieves the newest blog posts using traditional offset-based pagination with BlogDisplay format. " +
                      "Includes author information and comprehensive pagination metadata."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved newest blogs",
            content = @Content(schema = @Schema(implementation = BlogPageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/newest/paginated")
    public ResponseEntity<BlogPageResponse> getNewestBlogsWithPagination(
            @Parameter(description = "Page number (0-based)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of blogs per page", required = false)
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getNewestBlogsWithPagination(page, size));
    }

    /**
     * Get blog display information by ID
     *
     * Response Codes:
     * - 200 OK: Blog found with display information
     * - 404 NOT FOUND: Blog with the specified ID does not exist
     */
    @Operation(
        summary = "Get blog display by ID",
        description = "Retrieves a specific blog post by its ID in display format with author information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blog found",
            content = @Content(schema = @Schema(implementation = BlogDisplay.class))),
        @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    @GetMapping("/{id}/display")
    public ResponseEntity<BlogDisplay> getBlogDisplayById(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String id) {
        return blogService.getBlogDisplayById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
