package com.example.demo.web;

import com.example.demo.domain.Comment;
import com.example.demo.dto.CommentDto;
import com.example.demo.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comments/")
@AllArgsConstructor
public class CommentsController {
    CommentService commentService;
@PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentDto commentDto)
      {
         Comment comment=commentService.save(commentDto);
         return  new ResponseEntity<>(comment, HttpStatus.CREATED);
  }
  @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentDto>> getAllForPost(@PathVariable Long postId)
    {
        List<CommentDto> commentDtoList=commentService.getAllCommentsForPost(postId);
        return new ResponseEntity<>(commentDtoList,HttpStatus.OK);
    }
    @GetMapping("/by-user/{userName}")
    public ResponseEntity<List<CommentDto>> getAllForUser(@PathVariable String username)
    {
        List<CommentDto> commentDtoList=commentService.getAllCommentsForUser(username);
        return new ResponseEntity<>(commentDtoList,HttpStatus.OK);
    }
}
