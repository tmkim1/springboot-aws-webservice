package com.jojoidu.book.springboot.web.dto;

import com.jojoidu.book.springboot.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostSaveRequestDto {
    private String title;
    private String content;
    private String author;

    @Builder
    public PostSaveRequestDto(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Post toEntitiy() {
        return Post.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
