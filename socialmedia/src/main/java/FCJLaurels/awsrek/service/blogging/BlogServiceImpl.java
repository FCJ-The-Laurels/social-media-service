package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogPageResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogCursorResponse;
import FCJLaurels.awsrek.model.blog;
import FCJLaurels.awsrek.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

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

    @Override
    public Mono<BlogPageResponse> getPaginatedBlogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return blogRepository.findAllByOrderByCreationDateDesc(pageable)
                .map(this::maptoDTO)
                .collectList()
                .zipWith(blogRepository.count())
                .map(tuple -> {
                    var content = tuple.getT1();
                    var totalElements = tuple.getT2();
                    var totalPages = (int) Math.ceil((double) totalElements / size);

                    String nextCursor = null;
                    String previousCursor = null;

                    if (!content.isEmpty()) {
                        nextCursor = content.get(content.size() - 1).getCreationDate() != null
                                ? encodeCursor(content.get(content.size() - 1).getCreationDate())
                                : null;
                        previousCursor = content.get(0).getCreationDate() != null
                                ? encodeCursor(content.get(0).getCreationDate())
                                : null;
                    }

                    return BlogPageResponse.builder()
                            .content(content)
                            .page(page)
                            .size(size)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                            .hasNext(page < totalPages - 1)
                            .hasPrevious(page > 0)
                            .nextCursor(nextCursor)
                            .previousCursor(previousCursor)
                            .build();
                });
    }

    @Override
    public Mono<BlogCursorResponse> getBlogsByCursor(String cursor, int size) {
        // Fetch size + 1 to determine if there are more results
        int fetchSize = size + 1;
        Pageable pageable = PageRequest.of(0, fetchSize);

        Flux<blog> blogFlux;

        if (cursor == null || cursor.isEmpty()) {
            // First page - no cursor
            blogFlux = blogRepository.findAllByOrderByCreationDateDesc(pageable);
        } else {
            // Decode cursor to get the creation date
            LocalDateTime cursorDate = decodeCursor(cursor);
            blogFlux = blogRepository.findByCreationDateLessThanOrderByCreationDateDesc(cursorDate, pageable);
        }

        return blogFlux
                .map(this::maptoDTO)
                .collectList()
                .map(content -> {
                    boolean hasMore = content.size() > size;

                    // Remove the extra item if we fetched size + 1
                    if (hasMore) {
                        content = content.subList(0, size);
                    }

                    String nextCursor = null;
                    if (hasMore && !content.isEmpty()) {
                        nextCursor = encodeCursor(content.get(content.size() - 1).getCreationDate());
                    }

                    return BlogCursorResponse.builder()
                            .content(content)
                            .nextCursor(nextCursor)
                            .hasMore(hasMore)
                            .size(content.size())
                            .build();
                });
    }

    // Helper method to encode cursor (creation date as Base64)
    private String encodeCursor(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        String dateTimeStr = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return Base64.getEncoder().encodeToString(dateTimeStr.getBytes());
    }

    // Helper method to decode cursor
    private LocalDateTime decodeCursor(String cursor) {
        try {
            String decodedStr = new String(Base64.getDecoder().decode(cursor));
            return LocalDateTime.parse(decodedStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            // If cursor is invalid, return current time to start from beginning
            return LocalDateTime.now();
        }
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