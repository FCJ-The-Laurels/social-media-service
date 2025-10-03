package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.model.blog;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    // Create a new blog
    BlogDTO createBlog(BlogCreationDTO blogCreationDTO);

    // Get blog by ID
    Optional<BlogDTO> getBlogById(String id);

    // Get all blogs
    List<BlogDTO> getAllBlogs();

    // Get blogs by author
    List<BlogDTO> getBlogsByAuthor(String author);

    // Search blogs by title
    List<BlogDTO> searchBlogsByTitle(String title);

    // Update blog
    Optional<BlogDTO> updateBlog(String id, BlogEditDTO blogEditDTO);

    // Delete blog
    boolean deleteBlog(String id);

    // Increment like count
    BlogDTO incrementLikeCount(String id);

    // Decrement like count
    BlogDTO decrementLikeCount(String id);

    // Update comment count
    BlogDTO updateCommentCount(String id, long commentCount);

    // Convert entity to DTO
    BlogDTO convertToDTO(blog blog);

    // Convert DTO to entity
    blog convertToEntity(BlogCreationDTO blogCreationDTO);
}
