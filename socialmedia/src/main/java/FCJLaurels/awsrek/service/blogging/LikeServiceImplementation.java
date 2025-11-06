package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.likeDTO.LikeCreationDTO;
import FCJLaurels.awsrek.DTO.likeDTO.LikeDTO;
import FCJLaurels.awsrek.model.like;
import FCJLaurels.awsrek.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeServiceImplementation implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Override
    public LikeDTO createLike(LikeCreationDTO likeCreationDTO) {
        like newLike = like.builder()
                .blogId(likeCreationDTO.getBlogId())
                .userId(likeCreationDTO.getUserId())
                .build();

        like saved = likeRepository.save(newLike);
        return mapToDTO(saved);
    }

    @Override
    public Optional<LikeDTO> getLikeById(String id) {
        return likeRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public List<LikeDTO> getAllLikes() {
        return likeRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<LikeDTO> getLikesByBlogId(String blogId) {
        return likeRepository.findByBlogId(blogId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<LikeDTO> getLikesByUserId(String userId) {
        return likeRepository.findByUserId(userId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public boolean hasUserLikedBlog(String userId, String blogId) {
        return likeRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    @Override
    public Optional<LikeDTO> getLikeByUserIdAndBlogId(String userId, String blogId) {
        return likeRepository.findByUserIdAndBlogId(userId, blogId).map(this::mapToDTO);
    }

    @Override
    public boolean deleteLike(String id) {
        boolean exists = likeRepository.existsById(id);
        if (exists) {
            likeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteLikeByUserIdAndBlogId(String userId, String blogId) {
        boolean exists = likeRepository.existsByUserIdAndBlogId(userId, blogId);
        if (exists) {
            likeRepository.deleteByUserIdAndBlogId(userId, blogId);
            return true;
        }
        return false;
    }

    @Override
    public long deleteLikesByBlogId(String blogId) {
        return likeRepository.deleteByBlogId(blogId);
    }

    @Override
    public long countLikesByBlogId(String blogId) {
        return likeRepository.countByBlogId(blogId);
    }

    @Override
    public long countLikesByUserId(String userId) {
        return likeRepository.countByUserId(userId);
    }

    @Override
    public Optional<LikeDTO> toggleLike(String userId, String blogId) {
        boolean exists = likeRepository.existsByUserIdAndBlogId(userId, blogId);
        if (exists) {
            // Unlike: delete and return empty
            likeRepository.deleteByUserIdAndBlogId(userId, blogId);
            return Optional.empty();
        } else {
            like newLike = like.builder()
                    .blogId(blogId)
                    .userId(userId)
                    .build();
            like saved = likeRepository.save(newLike);
            return Optional.of(mapToDTO(saved));
        }
    }

    @Override
    public LikeDTO convertToDTO(like like) {
        return mapToDTO(like);
    }

    @Override
    public like convertToEntity(LikeCreationDTO likeCreationDTO) {
        return like.builder()
                .blogId(likeCreationDTO.getBlogId())
                .userId(likeCreationDTO.getUserId())
                .build();
    }

    private LikeDTO mapToDTO(like entity) {
        if (entity == null) return null;
        return LikeDTO.builder()
                .id(entity.getId())
                .blogId(entity.getBlogId())
                .userId(entity.getUserId())
                .creationDate(entity.getCreationDate())
                .build();
    }
}
