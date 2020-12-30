package com.example.demo.web;

import com.example.demo.domain.SubReddit;
import com.example.demo.dto.SubRedditDto;
import com.example.demo.service.SubRedditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubRedditController {
    SubRedditService subRedditService;
    @PostMapping
    public ResponseEntity<SubRedditDto> createSubReddit(@RequestBody SubRedditDto subRedditDto)
    {
      return  ResponseEntity.status(HttpStatus.CREATED).body(subRedditService.save(subRedditDto));
    }
    @GetMapping
    public ResponseEntity<List<SubRedditDto>> getAllSubReddits()
    {
         return  ResponseEntity.status(HttpStatus.CREATED).body(subRedditService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<SubRedditDto> getSubReddit(@RequestBody @PathVariable Long id)
    {
        SubRedditDto subRedditDto=subRedditService.getSingle(id);
        return new ResponseEntity<>(subRedditDto,HttpStatus.OK);
    }
}
