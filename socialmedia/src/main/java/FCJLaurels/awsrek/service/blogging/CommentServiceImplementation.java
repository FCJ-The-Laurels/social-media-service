package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.commentDTO.CommentCreationDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentEditDTO;
import FCJLaurels.awsrek.model.comment;
import FCJLaurels.awsrek.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImplementation implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public CommentDTO createComment(CommentCreationDTO commentCreationDTO) {
        comment newComment = comment.builder()
                .blogId(commentCreationDTO.getBlogId())
                .userId(commentCreationDTO.getUserId())
                .content(commentCreationDTO.getContent())
                .build();

        comment saved = commentRepository.save(newComment);
        return mapToDTO(saved);
    }

    @Override
    public Optional<CommentDTO> getCommentById(String id) {
        return commentRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getCommentsByBlogId(String blogId) {
        return commentRepository.findByBlogId(blogId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<CommentDTO> updateComment(String id, CommentEditDTO commentEditDTO) {
        Optional<comment> existing = commentRepository.findById(id);
        if (existing.isPresent()) {
            comment c = existing.get();
            c.setContent(commentEditDTO.getContent());
            comment saved = commentRepository.save(c);
            return Optional.of(mapToDTO(saved));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteComment(String id) {
        boolean exists = commentRepository.existsById(id);
        if (exists) {
            commentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public long deleteCommentsByBlogId(String blogId) {
        return commentRepository.deleteByBlogId(blogId);
    }

    @Override
    public long countCommentsByBlogId(String blogId) {
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
