package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogPageResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogCursorResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDisplay;
import FCJLaurels.awsrek.DTO.blogDTO.CursorPageDTO;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    // Create a new blog (with userId from header)
    BlogDTO createBlog(BlogCreationDTO blogCreationDTO, String userId);

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

    // Traditional offset-based pagination
    BlogPageResponse getPaginatedBlogs(int page, int size);

    // Cursor-based pagination for infinite scrolling (like Facebook/Amazon)
    BlogCursorResponse getBlogsByCursor(String cursor, int size);

    // Get newest blogs with BlogDisplay DTO and cursor pagination
    CursorPageDTO<BlogDisplay> getNewestBlogsWithCursor(String cursor, int size);

    // Get blog display by ID
    Optional<BlogDisplay> getBlogDisplayById(String id);

    // Get newest blogs with offset pagination using BlogDisplay
    BlogPageResponse getNewestBlogsWithPagination(int page, int size);
}
