package top.aiolife.record.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.aiolife.record.pojo.req.MovieReq;
import top.aiolife.record.service.IMovieService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class MovieServiceImplTest {

    @Autowired
    private IMovieService movieService;

    @Test
    public void testParseDouban() {
        // 用户提供的 URL (忠犬八公 - 电影)
        String urlMovie = "https://movie.douban.com/subject/3011091/";
        MovieReq resMovie = movieService.parseDouban(urlMovie);
        log.info("解析结果(电影): {}", resMovie);
        assertNotNull(resMovie.getTitle(), "电影标题不应为空");
        
        // 电视剧测试 (狂飙)
        String urlTv = "https://movie.douban.com/subject/35465232/";
        try {
            MovieReq resTv = movieService.parseDouban(urlTv);
            log.info("解析结果(电视剧): {}", resTv);
            assertNotNull(resTv.getTitle(), "电视剧标题不应为空");
        } catch (Exception e) {
            log.warn("解析电视剧失败(可能被封)", e);
        }
    }
}
