package FCJLaurels.awsrek.controller.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
     * - 500 INTERNAL SERVER ERROR: Server error during blog creation
     */
    @Operation(summary = "Create a new blog post", description = "Creates a new blog post with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Blog successfully created",
            content = @Content(schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public Mono<ResponseEntity<BlogDTO>> createBlog(@Valid @RequestBody BlogCreationDTO blogCreationDTO) {
        return blogService.createBlog(blogCreationDTO)
                .map(blog -> ResponseEntity.status(HttpStatus.CREATED).body(blog));
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
    public Mono<ResponseEntity<BlogDTO>> getBlogById(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
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
    public Flux<BlogDTO> getAllBlogs() {
        return blogService.getAllBlogs();
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
    public Flux<BlogDTO> getBlogsByAuthor(
            @Parameter(description = "Author name", required = true)
            @PathVariable String author) {
        return blogService.getBlogsByAuthor(author);
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
    public Flux<BlogDTO> searchBlogsByTitle(
            @Parameter(description = "Title search keyword", required = true)
            @RequestParam String title) {
        return blogService.searchBlogsByTitle(title);
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
    public Mono<ResponseEntity<BlogDTO>> updateBlog(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody BlogEditDTO blogEditDTO) {
        return blogService.updateBlog(id, blogEditDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
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
    public Mono<ResponseEntity<Void>> deleteBlog(
            @Parameter(description = "Blog ID", required = true)
            @PathVariable String id) {
        return blogService.deleteBlog(id)
                .map(deleted -> deleted ?
                    ResponseEntity.noContent().<Void>build() :
                    ResponseEntity.notFound().<Void>build());
    }

}
