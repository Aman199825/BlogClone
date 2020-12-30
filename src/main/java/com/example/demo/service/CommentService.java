package com.example.demo.service;

import com.example.demo.domain.Comment;
import com.example.demo.domain.NotificationEmail;
import com.example.demo.domain.Post;
import com.example.demo.domain.User;
import com.example.demo.dto.CommentDto;
import com.example.demo.exceptions.PostNotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;
    private final AuthService authService;
    private static final String POST_URL = "";
    public Comment save(CommentDto commentDto)
    {
        Comment comment=mapToComment(commentDto);
        User user=authService.getCurrentUser();
        Post post=postRepository.findById(commentDto.getPostId()).orElseThrow(()->new PostNotFoundException("post with id"+commentDto.getPostId()+" not found"));
        comment.setUser(user);
        comment.setPost(post);
      commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());
      return comment;
    }
    public  Comment mapToComment(CommentDto commentDto)
    {
        return Comment.builder().text(commentDto.getText()).createdAt(Instant.now()).build();
    }
    private void sendCommentNotification(String message, User user) {
        mailService.sendEmail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

    public List<CommentDto> getAllCommentsForPost(Long postId) {
        Post post=postRepository.findById(postId).orElseThrow(()->new PostNotFoundException("post with postId: "+postId+" not found"));
        List<CommentDto> commentDtoList=commentRepository.findByPost(post).stream().map(this::mapToDto).collect(Collectors.toList());
        return  commentDtoList;

    }

    public List<CommentDto> getAllCommentsForUser(String username) {
        User user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user with username: "+username+" not found"));
        List<CommentDto> commentDtoList=commentRepository.findByUser(user).stream().map(this::mapToDto).collect(Collectors.toList());
        return  commentDtoList;
    }
    public CommentDto mapToDto(Comment comment)
    {
        return  CommentDto.builder().postId(comment.getPost().getPostId()).text(comment.getText()).username(comment.getUser().getUsername()).createdDate(comment.getCreatedAt()).build();
    }
}
