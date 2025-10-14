package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogPageResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogCursorResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlogService {
    // Create a new blog
    Mono<BlogDTO> createBlog(BlogCreationDTO blogCreationDTO);

    // Get blog by ID
    Mono<BlogDTO> getBlogById(String id);

    // Get all blogs
    Flux<BlogDTO> getAllBlogs();

    // Get blogs by author
    Flux<BlogDTO> getBlogsByAuthor(String author);

    // Search blogs by title
    Flux<BlogDTO> searchBlogsByTitle(String title);

    // Update blog
    Mono<BlogDTO> updateBlog(String id, BlogEditDTO blogEditDTO);

    // Delete blog
    Mono<Boolean> deleteBlog(String id);

    // Traditional offset-based pagination
    Mono<BlogPageResponse> getPaginatedBlogs(int page, int size);

    // Cursor-based pagination for infinite scrolling (like Facebook/Amazon)
    Mono<BlogCursorResponse> getBlogsByCursor(String cursor, int size);
}
