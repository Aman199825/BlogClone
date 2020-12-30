package com.example.demo.service;

import com.example.demo.domain.Post;
import com.example.demo.domain.Vote;
import com.example.demo.dto.VoteDto;
import com.example.demo.exceptions.PostNotFoundException;
import com.example.demo.exceptions.SpringRedditException;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.demo.domain.VoteType.UPVOTE;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {
   private final PostRepository postRepository;
   private final AuthService authService;
   private final VoteRepository voteRepository;
    public void vote(VoteDto voteDto) {
        Post post=postRepository.findById(voteDto.getPostId()).orElseThrow(()-> new PostNotFoundException("post with id: "+voteDto.getPostId()+"not found"));
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if (voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType()
                        .equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already "
                    + voteDto.getVoteType() + "'d for this post");
        }

            Integer votecount=post.getVoteCount();
            if(votecount==null)
                votecount=0;
        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(votecount+ 1);
        } else {
            post.setVoteCount(votecount - 1);
        }
        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }
    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
