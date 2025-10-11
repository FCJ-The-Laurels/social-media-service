package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
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

    // Increment like count
    Mono<BlogDTO> incrementLikeCount(String id);

    // Decrement like count
    Mono<BlogDTO> decrementLikeCount(String id);

    // Update comment count
    Mono<BlogDTO> updateCommentCount(String id, long commentCount);
}
