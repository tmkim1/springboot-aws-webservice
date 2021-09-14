package com.jojoidu.book.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jojoidu.book.springboot.domain.post.Post;
import com.jojoidu.book.springboot.domain.post.PostRepository;
import com.jojoidu.book.springboot.web.dto.PostSaveRequestDto;
import com.jojoidu.book.springboot.web.dto.PostUpdateRequestDto;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostApiControllerTest extends TestCase {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    /**
     * 매번 테스트가 시작되기 전에 MockMvc 인스턴스를 생성
     */
    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        postRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER") //@SpringBootTest에서는 작동하지 않음 => MockMvc 사용
    public void Post_등록된다() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostSaveRequestDto requestDto = PostSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("kim")
                .build();

        String url = "http://localhost:" + port + "/api/v1/post";

        //when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        //then
        List<Post> posts = postRepository.findAll();

        assertThat(posts.get(0).getTitle()).isEqualTo(title);
        assertThat(posts.get(0).getContent()).isEqualTo(content);
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void Post_수정된다() throws Exception {
        //given
        Post savedPost = postRepository.save(Post.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPost.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostUpdateRequestDto requestDto = PostUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/post/" + updateId;

        HttpEntity<PostUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        List<Post> posts = postRepository.findAll();

        assertThat(posts.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(posts.get(0).getContent()).isEqualTo(expectedContent);
    }
}