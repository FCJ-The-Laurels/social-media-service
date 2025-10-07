package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.model.blog;
import FCJLaurels.awsrek.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Override
    public BlogDTO createBlog(BlogCreationDTO blogCreationDTO) {
        blog newBlog = new blog();
        newBlog.setTitle(blogCreationDTO.getTitle());
        newBlog.setContent(blogCreationDTO.getContent());
        newBlog.setAuthor(blogCreationDTO.getAuthor());
        newBlog.setLikeCount(0);
        newBlog.setCommentCount(0);
        blog savedBlog = blogRepository.save(newBlog);
        return maptoDTO(savedBlog);
    }

    @Override
    public Optional<BlogDTO> getBlogById(String id) {
        return blogRepository.findById(id).map(this::maptoDTO);
    }

    @Override
    public List<BlogDTO> getAllBlogs() {
        return blogRepository.findAll().stream().map(this::maptoDTO).collect(Collectors.toList());
    }

    @Override
    public List<BlogDTO> getBlogsByAuthor(String author) {
        List<blog> blogs = blogRepository.findByAuthor(author);
        return blogs.stream()
                .map(this::maptoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogDTO> searchBlogsByTitle(String title) {
        List<blog> blogs = blogRepository.findByTitleContainingIgnoreCase(title);
        return blogs.stream()
                .map(this::maptoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BlogDTO> updateBlog(String id, BlogEditDTO blogEditDTO) {
        Optional<blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            blog b = optionalBlog.get();
            b.setTitle(blogEditDTO.getTitle());
            b.setContent(blogEditDTO.getContent());
            blog updated = blogRepository.save(b);
            return Optional.of(maptoDTO(updated));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteBlog(String id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public BlogDTO incrementLikeCount(String id) {
        Optional<blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            blog b = optionalBlog.get();
            b.setLikeCount(b.getLikeCount() + 1);
            blog updated = blogRepository.save(b);
            return maptoDTO(updated);
        }
        return null;
    }

    @Override
    public BlogDTO decrementLikeCount(String id) {
        Optional<blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            blog b = optionalBlog.get();
            b.setLikeCount(Math.max(0, b.getLikeCount() - 1));
            blog updated = blogRepository.save(b);
            return maptoDTO(updated);
        }
        return null;
    }

    @Override
    public BlogDTO updateCommentCount(String id, long commentCount) {
        Optional<blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            blog b = optionalBlog.get();
            b.setCommentCount(commentCount);
            blog updated = blogRepository.save(b);
            return maptoDTO(updated);
        }
        return null;
    }

    private BlogDTO maptoDTO(blog entity) {
        if (entity == null) return null;
        return BlogDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .author(entity.getAuthor())
                .creationDate(entity.getCreationDate())
                .imageUrl(entity.getImageUrl())
                .likeCount(entity.getLikeCount())
                .commentCount(entity.getCommentCount())
                .build();
    }
}