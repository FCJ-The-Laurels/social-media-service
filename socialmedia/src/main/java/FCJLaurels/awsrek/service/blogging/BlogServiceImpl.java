package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.blogDTO.BlogCreationDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogEditDTO;
import FCJLaurels.awsrek.DTO.blogDTO.BlogPageResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogCursorResponse;
import FCJLaurels.awsrek.DTO.blogDTO.BlogDisplay;
import FCJLaurels.awsrek.DTO.blogDTO.CursorPageDTO;
import FCJLaurels.awsrek.model.blog;
import FCJLaurels.awsrek.repository.BlogRepository;
import FCJLaurels.awsrek.service.MetricsService;
import FCJLaurels.awsrek.service.UserGrpcClientService;
import FCJ.user.grpc.BlogUserInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private UserGrpcClientService userGrpcClientService;

    @Override
    public BlogDTO createBlog(BlogCreationDTO blogCreationDTO, String userId) {
        log.debug("Creating blog for userId: {}", userId);

        if (userId == null || userId.isEmpty()) {
            log.error("User ID is null or empty");
            throw new IllegalArgumentException("User ID is required to create a blog");
        }

        try {
            blog newBlog = new blog();
            newBlog.setTitle(blogCreationDTO.getTitle());
            newBlog.setContent(blogCreationDTO.getContent());
            newBlog.setAuthor(UUID.fromString(userId));
            newBlog.setImageUrl(blogCreationDTO.getImageUrl());

            blog saved = blogRepository.save(newBlog);
            log.info("Blog created successfully with id: {}", saved.getId());

            if (metricsService != null) metricsService.incrementBlogCreated();
            return maptoDTO(saved);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for userId: {}", userId, e);
            throw new IllegalArgumentException("Invalid user ID format", e);
        } catch (Exception e) {
            log.error("Error creating blog for userId: {}", userId, e);
            if (metricsService != null) metricsService.incrementApiError("BlogCreationError");
            throw new RuntimeException("Failed to create blog", e);
        }
    }

    @Override
    public Optional<BlogDTO> getBlogById(String id) {
        log.debug("Fetching blog by id: {}", id);
        try {
            return blogRepository.findById(id).map(this::maptoDTO);
        } catch (Exception e) {
            log.error("Error fetching blog by id: {}", id, e);
            if (metricsService != null) metricsService.incrementApiError("BlogRetrievalError");
            return Optional.empty();
        }
    }

    @Override
    public List<BlogDTO> getAllBlogs() {
        log.debug("Fetching all blogs");
        try {
            List<BlogDTO> blogs = blogRepository.findAll().stream().map(this::maptoDTO).collect(Collectors.toList());
            log.info("Retrieved {} blogs", blogs.size());
            return blogs;
        } catch (Exception e) {
            log.error("Error fetching all blogs", e);
            if (metricsService != null) metricsService.incrementApiError("BlogListError");
            return List.of();
        }
    }

    @Override
    public List<BlogDTO> getBlogsByAuthor(String author) {
        log.debug("Fetching blogs by author: {}", author);
        try {
            // Convert string to UUID if it's a valid UUID format
            UUID authorUuid = UUID.fromString(author);
            List<BlogDTO> blogs = blogRepository.findByAuthor(authorUuid).stream()
                    .map(this::maptoDTO)
                    .collect(Collectors.toList());
            log.info("Retrieved {} blogs for author: {}", blogs.size(), author);
            return blogs;
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for author: {}", author, e);
            if (metricsService != null) metricsService.incrementApiError("BlogsByAuthorError");
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching blogs by author: {}", author, e);
            if (metricsService != null) metricsService.incrementApiError("BlogsByAuthorError");
            return List.of();
        }
    }

    @Override
    public List<BlogDTO> searchBlogsByTitle(String title) {
        log.debug("Searching blogs by title: {}", title);
        try {
            return blogRepository.findByTitleContainingIgnoreCase(title).stream().map(this::maptoDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching blogs by title: {}", title, e);
            if (metricsService != null) metricsService.incrementApiError("BlogSearchError");
            return List.of();
        }
    }

    @Override
    public Optional<BlogDTO> updateBlog(String id, BlogEditDTO blogEditDTO) {
        log.debug("Updating blog with id: {}", id);
        try {
            Optional<blog> existing = blogRepository.findById(id);
            if (existing.isPresent()) {
                blog b = existing.get();
                b.setTitle(blogEditDTO.getTitle());
                b.setContent(blogEditDTO.getContent());
                blog saved = blogRepository.save(b);
                log.info("Blog updated successfully with id: {}", saved.getId());
                return Optional.of(maptoDTO(saved));
            }
            log.warn("Blog not found for update: {}", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error updating blog with id: {}", id, e);
            if (metricsService != null) metricsService.incrementApiError("BlogUpdateError");
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteBlog(String id) {
        log.debug("Deleting blog with id: {}", id);
        try {
            boolean exists = blogRepository.existsById(id);
            if (exists) {
                blogRepository.deleteById(id);
                log.info("Blog deleted successfully: {}", id);
                if (metricsService != null) metricsService.incrementBlogDeleted();
                return true;
            }
            log.warn("Blog not found for deletion: {}", id);
            return false;
        } catch (Exception e) {
            log.error("Error deleting blog: {}", id, e);
            if (metricsService != null) metricsService.incrementApiError("BlogDeletionError");
            return false;
        }
    }

    @Override
    public BlogPageResponse getPaginatedBlogs(int page, int size) {
        log.debug("Fetching paginated blogs - page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);

            List<blog> blogs = blogRepository.findAllByOrderByCreationDateDesc(pageable);
            List<BlogDisplay> content = blogs.stream().map(this::mapToBlogDisplay).collect(Collectors.toList());
            long totalElements = blogRepository.count();
            int totalPages = (int) Math.ceil((double) totalElements / size);

            String nextCursor = null;
            String previousCursor = null;

            if (!content.isEmpty() && !blogs.isEmpty()) {
                nextCursor = encodeCursor(blogs.get(blogs.size() - 1).getCreationDate());
                previousCursor = encodeCursor(blogs.get(0).getCreationDate());
            }

            log.info("Retrieved {} blogs for page {}", content.size(), page);

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
        } catch (Exception e) {
            log.error("Error fetching paginated blogs", e);
            if (metricsService != null) metricsService.incrementApiError("BlogPagePaginationError");
            return BlogPageResponse.builder()
                    .content(List.of())
                    .page(page)
                    .size(size)
                    .totalElements(0)
                    .totalPages(0)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();
        }
    }

    @Override
    public BlogCursorResponse getBlogsByCursor(String cursor, int size) {
        log.debug("Fetching blogs by cursor - cursor: {}, size: {}", cursor, size);
        int fetchSize = size + 1;
        Pageable pageable = PageRequest.of(0, fetchSize);

        List<blog> blogList;
        if (cursor == null || cursor.isEmpty()) {
            blogList = blogRepository.findAllByOrderByCreationDateDesc(pageable);
        } else {
            LocalDateTime cursorDate = decodeCursor(cursor);
            blogList = blogRepository.findByCreationDateLessThanOrderByCreationDateDesc(cursorDate, pageable);
        }

        List<BlogDisplay> content = blogList.stream()
                .limit(size)
                .map(this::mapToBlogDisplay)
                .collect(Collectors.toList());
        boolean hasMore = blogList.size() > size;

        String nextCursor = null;
        if (hasMore && !content.isEmpty()) {
            nextCursor = encodeCursor(blogList.get(size - 1).getCreationDate());
        }

        log.info("Retrieved {} blogs with cursor {}", content.size(), cursor);

        return BlogCursorResponse.builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .size(content.size())
                .build();
    }

    @Override
    public CursorPageDTO<BlogDisplay> getNewestBlogsWithCursor(String cursor, int size) {
        log.debug("Fetching newest blogs with cursor - cursor: {}, size: {}", cursor, size);
        try {
            int fetchSize = size + 1; // Fetch one extra to check if there are more
            Pageable pageable = PageRequest.of(0, fetchSize);

            List<blog> blogList;
            if (cursor == null || cursor.isEmpty()) {
                // First request - get newest blogs
                blogList = blogRepository.findAllByOrderByCreationDateDesc(pageable);
            } else {
                // Subsequent request - get blogs older than cursor
                LocalDateTime cursorDate = decodeCursor(cursor);
                blogList = blogRepository.findByCreationDateLessThanOrderByCreationDateDesc(cursorDate, pageable);
            }

            // Map to BlogDisplay DTOs
            List<BlogDisplay> content = blogList.stream()
                    .limit(size) // Only take requested size
                    .map(this::mapToBlogDisplay)
                    .collect(Collectors.toList());

            // Check if there are more results
            boolean hasMore = blogList.size() > size;

            // Generate next cursor from the last item
            String nextCursor = null;
            if (hasMore && !content.isEmpty()) {
                LocalDateTime lastDate = blogList.get(size - 1).getCreationDate();
                nextCursor = encodeCursor(lastDate);
            }

            log.info("Retrieved {} newest blogs with cursor {}", content.size(), cursor);

            return new CursorPageDTO<>(content, nextCursor);
        } catch (Exception e) {
            log.error("Error fetching newest blogs with cursor", e);
            if (metricsService != null) metricsService.incrementApiError("BlogCursorPaginationError");
            return new CursorPageDTO<>(List.of(), null);
        }
    }

    @Override
    public Optional<BlogDisplay> getBlogDisplayById(String id) {
        log.debug("Fetching blog display by id: {}", id);
        try {
            return blogRepository.findById(id).map(this::mapToBlogDisplay);
        } catch (Exception e) {
            log.error("Error fetching blog display by id: {}", id, e);
            if (metricsService != null) metricsService.incrementApiError("BlogDisplayRetrievalError");
            return Optional.empty();
        }
    }

    @Override
    public BlogPageResponse getNewestBlogsWithPagination(int page, int size) {
        log.debug("Fetching newest blogs with pagination - page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);

            List<blog> blogs = blogRepository.findAllByOrderByCreationDateDesc(pageable);
            List<BlogDisplay> content = blogs.stream()
                    .map(this::mapToBlogDisplay)
                    .collect(Collectors.toList());

            long totalElements = blogRepository.count();
            int totalPages = (int) Math.ceil((double) totalElements / size);

            String nextCursor = null;
            String previousCursor = null;

            if (!content.isEmpty() && !blogs.isEmpty()) {
                nextCursor = encodeCursor(blogs.get(blogs.size() - 1).getCreationDate());
                previousCursor = encodeCursor(blogs.get(0).getCreationDate());
            }

            log.info("Retrieved {} newest blogs for page {}", content.size(), page);

            return BlogPageResponse.builder()
                    .content(content) // Now contains BlogDisplay objects
                    .page(page)
                    .size(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .hasNext(page < totalPages - 1)
                    .hasPrevious(page > 0)
                    .nextCursor(nextCursor)
                    .previousCursor(previousCursor)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching newest blogs with pagination", e);
            if (metricsService != null) metricsService.incrementApiError("BlogPagePaginationError");
            return BlogPageResponse.builder()
                    .content(List.of())
                    .page(page)
                    .size(size)
                    .totalElements(0)
                    .totalPages(0)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();
        }
    }

    // Helper method to encode cursor (creation date as Base64)
    private String encodeCursor(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        String dateTimeStr = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return Base64.getEncoder().encodeToString(dateTimeStr.getBytes());
    }

    // Helper method to decode cursor with better error handling
    private LocalDateTime decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            log.warn("Cursor is null or empty, returning null");
            return null;
        }

        try {
            String decodedStr = new String(Base64.getDecoder().decode(cursor));
            LocalDateTime dateTime = LocalDateTime.parse(decodedStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            log.debug("Decoded cursor: {}", dateTime);
            return dateTime;
        } catch (Exception e) {
            log.error("Error decoding cursor: {}", cursor, e);
            if (metricsService != null) metricsService.incrementApiError("CursorDecodingError");
            // Return null instead of LocalDateTime.now() to avoid incorrect pagination
            return null;
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

    // New mapping method for BlogDisplay with gRPC user info fetching
    // This calls the blogUserInfo() RPC method from the user service
    private BlogDisplay mapToBlogDisplay(blog entity) {
        if (entity == null) return null;

        String authorName = null;
        String authorAvatar = null;

        // Fetch author information using gRPC (BLOCKING - synchronous)
        // Calls the blogUserInfo() method from UserInfoService in user service
        if (entity.getAuthor() != null) {
            try {
                // Convert UUID to String format for gRPC call
                // The user service expects UUID as a string (e.g., "550e8400-e29b-41d4-a716-446655440000")
                String authorIdString = entity.getAuthor().toString();

                log.debug("🔄 Converting UUID author to string: {} (type: {})",
                    authorIdString, entity.getAuthor().getClass().getSimpleName());

                // Call the gRPC service via UserGrpcClientService
                // This internally calls blogUserInfo(BlogUserInfoRequest) with the UUID string
                log.info("📞 Calling gRPC blogUserInfo() to fetch user info for author UUID: {}", authorIdString);
                BlogUserInfoResponse userInfo = userGrpcClientService.getUserInfo(authorIdString);

                if (userInfo != null) {
                    // Extract only the required fields from BlogUserInfoResponse
                    // according to the proto contract: string name=1; string avatar=2;
                    authorName = userInfo.getName();
                    authorAvatar = userInfo.getAvatar();

                    log.info("✅ gRPC blogUserInfo() fetched successfully - name: '{}', avatar: '{}'",
                        authorName, authorAvatar);
                } else {
                    log.warn("⚠️  gRPC blogUserInfo() returned null response for author UUID: {}", authorIdString);
                }
            } catch (Exception e) {
                log.error("❌ Error calling gRPC blogUserInfo() for author UUID: {}",
                    entity.getAuthor(), e);
                if (metricsService != null) {
                    metricsService.incrementApiError("BlogUserInfoFetchError");
                }
            }
        }

        return BlogDisplay.builder()
                .id(entity.getId())
                .authorName(authorName != null && !authorName.isEmpty() ? authorName : "Unknown User")
                .authorAvatar(authorAvatar)
                .title(entity.getTitle())
                .imageURL(entity.getImageUrl())
                .content(entity.getContent())
                .creationDate(entity.getCreationDate())
                .build();
    }
}

