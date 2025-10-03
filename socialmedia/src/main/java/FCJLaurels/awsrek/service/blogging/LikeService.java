package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.likeDTO.LikeCreationDTO;
import FCJLaurels.awsrek.DTO.likeDTO.LikeDTO;
import FCJLaurels.awsrek.model.like;

import java.util.List;
import java.util.Optional;

public interface LikeService {
    // Create a new like (user likes a blog)
    LikeDTO createLike(LikeCreationDTO likeCreationDTO);

    // Get like by ID
    Optional<LikeDTO> getLikeById(String id);

    // Get all likes
    List<LikeDTO> getAllLikes();

    // Get likes by blog ID
    List<LikeDTO> getLikesByBlogId(String blogId);

    // Get likes by user ID
    List<LikeDTO> getLikesByUserId(String userId);

    // Check if user has liked a specific blog
    boolean hasUserLikedBlog(String userId, String blogId);

    // Get specific like by user ID and blog ID
    Optional<LikeDTO> getLikeByUserIdAndBlogId(String userId, String blogId);

    // Delete like (user unlikes a blog)
    boolean deleteLike(String id);

    // Delete like by user ID and blog ID
    boolean deleteLikeByUserIdAndBlogId(String userId, String blogId);

    // Delete all likes by blog ID (useful when deleting a blog)
    long deleteLikesByBlogId(String blogId);

    // Count likes by blog ID
    long countLikesByBlogId(String blogId);

    // Count likes by user ID
    long countLikesByUserId(String userId);

    // Toggle like (like if not liked, unlike if already liked)
    LikeDTO toggleLike(String userId, String blogId);

    // Convert entity to DTO
    LikeDTO convertToDTO(like like);

    // Convert DTO to entity
    like convertToEntity(LikeCreationDTO likeCreationDTO);
}
