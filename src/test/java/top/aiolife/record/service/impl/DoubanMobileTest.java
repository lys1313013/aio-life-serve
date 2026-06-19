package top.aiolife.record.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

public class DoubanMobileTest {
    @Test
    public void testHtml() throws Exception {
        String url2 = "https://m.douban.com/rexxar/api/v2/tv/35815340";
        Document doc2 = Jsoup.connect(url2)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
                .header("Referer", "https://m.douban.com/movie/subject/35815340/")
                .get();
        System.out.println("----TV JSON START----");
        System.out.println(doc2.body().text());
        System.out.println("----TV JSON END----");
    }
}
