package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.likeDTO.LikeCreationDTO;
import FCJLaurels.awsrek.DTO.likeDTO.LikeDTO;
import FCJLaurels.awsrek.model.like;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LikeService {
    // Create a new like (user likes a blog)
    Mono<LikeDTO> createLike(LikeCreationDTO likeCreationDTO);

    // Get like by ID
    Mono<LikeDTO> getLikeById(String id);

    // Get all likes
    Flux<LikeDTO> getAllLikes();

    // Get likes by blog ID
    Flux<LikeDTO> getLikesByBlogId(String blogId);

    // Get likes by user ID
    Flux<LikeDTO> getLikesByUserId(String userId);

    // Check if user has liked a specific blog
    Mono<Boolean> hasUserLikedBlog(String userId, String blogId);

    // Get specific like by user ID and blog ID
    Mono<LikeDTO> getLikeByUserIdAndBlogId(String userId, String blogId);

    // Delete like (user unlikes a blog)
    Mono<Boolean> deleteLike(String id);

    // Delete like by user ID and blog ID
    Mono<Boolean> deleteLikeByUserIdAndBlogId(String userId, String blogId);

    // Delete all likes by blog ID (useful when deleting a blog)
    Mono<Long> deleteLikesByBlogId(String blogId);

    // Count likes by blog ID
    Mono<Long> countLikesByBlogId(String blogId);

    // Count likes by user ID
    Mono<Long> countLikesByUserId(String userId);

    // Toggle like (like if not liked, unlike if already liked)
    Mono<LikeDTO> toggleLike(String userId, String blogId);

    // Convert entity to DTO
    LikeDTO convertToDTO(like like);

    // Convert DTO to entity
    like convertToEntity(LikeCreationDTO likeCreationDTO);
}
