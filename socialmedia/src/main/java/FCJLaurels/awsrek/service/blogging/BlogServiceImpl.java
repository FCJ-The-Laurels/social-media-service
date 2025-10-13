package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.model.blog;
import FCJLaurels.awsrek.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Mono<BlogDTO> createBlog(BlogCreationDTO blogCreationDTO) {
        blog newBlog = new blog();
        newBlog.setTitle(blogCreationDTO.getTitle());
        newBlog.setContent(blogCreationDTO.getContent());
        newBlog.setAuthor(blogCreationDTO.getAuthor());
        newBlog.setImageUrl(blogCreationDTO.getImageUrl());

        return blogRepository.save(newBlog)
                .map(this::maptoDTO);
    }

    @Override
    public Mono<BlogDTO> getBlogById(String id) {
        return blogRepository.findById(id)
                .map(this::maptoDTO);
    }

    @Override
    public Flux<BlogDTO> getAllBlogs() {
        return blogRepository.findAll()
                .map(this::maptoDTO);
    }

    @Override
    public Flux<BlogDTO> getBlogsByAuthor(String author) {
        return blogRepository.findByAuthor(author)
                .map(this::maptoDTO);
    }

    @Override
    public Flux<BlogDTO> searchBlogsByTitle(String title) {
        return blogRepository.findByTitleContainingIgnoreCase(title)
                .map(this::maptoDTO);
    }

    @Override
    public Mono<BlogDTO> updateBlog(String id, BlogEditDTO blogEditDTO) {
        return blogRepository.findById(id)
                .flatMap(b -> {
                    b.setTitle(blogEditDTO.getTitle());
                    b.setContent(blogEditDTO.getContent());
                    return blogRepository.save(b);
                })
                .map(this::maptoDTO);
    }

    @Override
    public Mono<Boolean> deleteBlog(String id) {
        return blogRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return blogRepository.deleteById(id)
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                });
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
                .build();
    }
}