package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.commentDTO.CommentCreationDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentEditDTO;
import FCJLaurels.awsrek.model.comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    // Create a new comment
    CommentDTO createComment(CommentCreationDTO commentCreationDTO);

    // Get comment by ID
    Optional<CommentDTO> getCommentById(String id);

    // Get all comments
    List<CommentDTO> getAllComments();

    // Get comments by blog ID
    List<CommentDTO> getCommentsByBlogId(String blogId);

    // Get comments by user ID
    List<CommentDTO> getCommentsByUserId(String userId);

    // Update comment
    Optional<CommentDTO> updateComment(String id, CommentEditDTO commentEditDTO);

    // Delete comment
    boolean deleteComment(String id);

    // Delete all comments by blog ID (useful when deleting a blog)
    long deleteCommentsByBlogId(String blogId);

    // Count comments by blog ID
    long countCommentsByBlogId(String blogId);

    // Convert entity to DTO
    CommentDTO convertToDTO(comment comment);

    // Convert DTO to entity
    comment convertToEntity(CommentCreationDTO commentCreationDTO);
}
