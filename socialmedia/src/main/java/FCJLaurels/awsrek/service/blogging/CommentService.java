package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.commentDTO.CommentCreationDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentDTO;
import FCJLaurels.awsrek.DTO.commentDTO.CommentEditDTO;
import FCJLaurels.awsrek.model.comment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {
    // Create a new comment
    Mono<CommentDTO> createComment(CommentCreationDTO commentCreationDTO);

    // Get comment by ID
    Mono<CommentDTO> getCommentById(String id);

    // Get all comments
    Flux<CommentDTO> getAllComments();

    // Get comments by blog ID
    Flux<CommentDTO> getCommentsByBlogId(String blogId);

    // Get comments by user ID
    Flux<CommentDTO> getCommentsByUserId(String userId);

    // Update comment
    Mono<CommentDTO> updateComment(String id, CommentEditDTO commentEditDTO);

    // Delete comment
    Mono<Boolean> deleteComment(String id);

    // Delete all comments by blog ID (useful when deleting a blog)
    Mono<Long> deleteCommentsByBlogId(String blogId);

    // Count comments by blog ID
    Mono<Long> countCommentsByBlogId(String blogId);

    // Convert entity to DTO
    CommentDTO convertToDTO(comment comment);

    // Convert DTO to entity
    comment convertToEntity(CommentCreationDTO commentCreationDTO);
}
