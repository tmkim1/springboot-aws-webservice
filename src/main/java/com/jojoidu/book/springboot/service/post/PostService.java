package com.jojoidu.book.springboot.service.post;

import com.jojoidu.book.springboot.domain.post.PostRepository;
import com.jojoidu.book.springboot.web.dto.PostSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Long save(PostSaveRequestDto requestDto) {
        return postRepository.save(requestDto.toEntitiy()).getId();
    }
}
