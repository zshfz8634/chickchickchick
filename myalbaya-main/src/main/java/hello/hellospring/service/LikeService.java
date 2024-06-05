package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.domain.Post;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public LikeService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public void likePost(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (post.getLikes().contains(member)) {
            post.getLikes().remove(member);
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            post.getLikes().add(member);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        postRepository.save(post);
    }
}
