package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.commentDTO.CommentCreationDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentEditDTO;
import FCJLaurels.awsrek.model.comment;
import FCJLaurels.awsrek.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommentServiceImplementation implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Mono<CommentDTO> createComment(CommentCreationDTO commentCreationDTO) {
        comment newComment = comment.builder()
                .blogId(commentCreationDTO.getBlogId())
                .userId(commentCreationDTO.getUserId())
                .content(commentCreationDTO.getContent())
                .build();

        return commentRepository.save(newComment)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<CommentDTO> getCommentById(String id) {
        return commentRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public Flux<CommentDTO> getAllComments() {
        return commentRepository.findAll()
                .map(this::mapToDTO);
    }

    @Override
    public Flux<CommentDTO> getCommentsByBlogId(String blogId) {
        return commentRepository.findByBlogId(blogId)
                .map(this::mapToDTO);
    }

    @Override
    public Flux<CommentDTO> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<CommentDTO> updateComment(String id, CommentEditDTO commentEditDTO) {
        return commentRepository.findById(id)
                .flatMap(c -> {
                    c.setContent(commentEditDTO.getContent());
                    return commentRepository.save(c);
                })
                .map(this::mapToDTO);
    }

    @Override
    public Mono<Boolean> deleteComment(String id) {
        return commentRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return commentRepository.deleteById(id)
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<Long> deleteCommentsByBlogId(String blogId) {
        return commentRepository.findByBlogId(blogId)
                .flatMap(c -> commentRepository.deleteById(c.getId()).thenReturn(1L))
                .reduce(0L, Long::sum);
    }

    @Override
    public Mono<Long> countCommentsByBlogId(String blogId) {
        return commentRepository.countByBlogId(blogId);
    }

    @Override
    public CommentDTO convertToDTO(comment comment) {
        return mapToDTO(comment);
    }

    @Override
    public comment convertToEntity(CommentCreationDTO commentCreationDTO) {
        return comment.builder()
                .blogId(commentCreationDTO.getBlogId())
                .userId(commentCreationDTO.getUserId())
                .content(commentCreationDTO.getContent())
                .build();
    }

    private CommentDTO mapToDTO(comment entity) {
        if (entity == null) return null;
        return CommentDTO.builder()
                .id(entity.getId())
                .blogId(entity.getBlogId())
                .userId(entity.getUserId())
                .creationDate(entity.getCreationDate())
                .content(entity.getContent())
                .build();
    }
}
