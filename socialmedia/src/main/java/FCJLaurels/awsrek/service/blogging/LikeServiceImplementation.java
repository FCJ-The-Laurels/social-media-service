package FCJLaurels.awsrek.service.blogging;

import FCJLaurels.awsrek.DTO.likeDTO.LikeCreationDTO;
import FCJLaurels.awsrek.DTO.likeDTO.LikeDTO;
import FCJLaurels.awsrek.model.like;
import FCJLaurels.awsrek.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LikeServiceImplementation implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Override
    public Mono<LikeDTO> createLike(LikeCreationDTO likeCreationDTO) {
        like newLike = like.builder()
                .blogId(likeCreationDTO.getBlogId())
                .userId(likeCreationDTO.getUserId())
                .build();

        return likeRepository.save(newLike)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<LikeDTO> getLikeById(String id) {
        return likeRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public Flux<LikeDTO> getAllLikes() {
        return likeRepository.findAll()
                .map(this::mapToDTO);
    }

    @Override
    public Flux<LikeDTO> getLikesByBlogId(String blogId) {
        return likeRepository.findByBlogId(blogId)
                .map(this::mapToDTO);
    }

    @Override
    public Flux<LikeDTO> getLikesByUserId(String userId) {
        return likeRepository.findByUserId(userId)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<Boolean> hasUserLikedBlog(String userId, String blogId) {
        return likeRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    @Override
    public Mono<LikeDTO> getLikeByUserIdAndBlogId(String userId, String blogId) {
        return likeRepository.findByUserIdAndBlogId(userId, blogId)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<Boolean> deleteLike(String id) {
        return likeRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return likeRepository.deleteById(id)
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<Boolean> deleteLikeByUserIdAndBlogId(String userId, String blogId) {
        return likeRepository.existsByUserIdAndBlogId(userId, blogId)
                .flatMap(exists -> {
                    if (exists) {
                        return likeRepository.deleteByUserIdAndBlogId(userId, blogId)
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<Long> deleteLikesByBlogId(String blogId) {
        return likeRepository.deleteByBlogId(blogId);
    }

    @Override
    public Mono<Long> countLikesByBlogId(String blogId) {
        return likeRepository.countByBlogId(blogId);
    }

    @Override
    public Mono<Long> countLikesByUserId(String userId) {
        return likeRepository.countByUserId(userId);
    }

    @Override
    public Mono<LikeDTO> toggleLike(String userId, String blogId) {
        return likeRepository.existsByUserIdAndBlogId(userId, blogId)
                .flatMap(exists -> {
                    if (exists) {
                        // Unlike - delete the like and return empty
                        return likeRepository.deleteByUserIdAndBlogId(userId, blogId)
                                .then(Mono.empty());
                    } else {
                        // Like - create new like
                        like newLike = like.builder()
                                .blogId(blogId)
                                .userId(userId)
                                .build();
                        return likeRepository.save(newLike)
                                .map(this::mapToDTO);
                    }
                });
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
