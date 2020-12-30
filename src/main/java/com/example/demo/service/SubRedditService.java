package com.example.demo.service;

import com.example.demo.domain.SubReddit;
import com.example.demo.dto.SubRedditDto;
import com.example.demo.exceptions.SpringRedditException;
import com.example.demo.repository.SubRedditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubRedditService {
    SubRedditRepository subRedditRepository;
    @Transactional
    public SubRedditDto save(SubRedditDto subRedditDto)
    {
        SubReddit subReddit=mapSubRedditDto(subRedditDto);
        subRedditRepository.save(subReddit);
        subRedditDto.setId(subReddit.getId());
        return  subRedditDto;
    }
    private SubReddit mapSubRedditDto(SubRedditDto subRedditDto)
    {
        return  SubReddit.builder().name(subRedditDto.getName()).description(subRedditDto.getDescription()).build();
    }
    @Transactional(readOnly =true)
    public List<SubRedditDto> getAll() {
        return subRedditRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }
    public SubRedditDto getSingle(Long id)
    {
        SubReddit subReddit=subRedditRepository.findById(id).orElseThrow(()->new SpringRedditException("subreddit with id: "+id+" doesn't exists"));
        return mapToDto(subReddit);
    }
    private SubRedditDto mapToDto(SubReddit subReddit)
    {
         return SubRedditDto.builder().name(subReddit.getName()).id(subReddit.getId()).postCount(subReddit.getPosts().size()).build();
    }
}
