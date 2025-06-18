package com.lys.datasource;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/06/18 23:30
 */
@Service
public class ZhihuDataSource {
    @Value("${aio.api.zhihu}")
    private String cookie;

     public List<HotPostDataVO> hotList() {
         String urlZhiHu = "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50&desktop=true";
         // 带上请求头
         String result = HttpRequest.get(urlZhiHu)
                 .header("cookie", cookie)
                 .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                 .header("referer", "https://www.zhihu.com/hot")
                 .header("accept", "application/json, text/plain, */*")
                 .header("accept-language", "zh-CN,zh;q=0.9")
                 .execute()
                 .body();
         System.out.println(result);
         JSONObject resultJson = (JSONObject) JSON.parse(result);
         JSONArray data = resultJson.getJSONArray("data");
         List<HotPostDataVO> dataList = data.stream().map(item -> {
             JSONObject jsonItem = (JSONObject) item;
             JSONObject target = jsonItem.getJSONObject("target");
             String title = target.getString("title");
             String[] parts = target.getString("url").split("/");
             String url = "https://zhihu.com/question/" + parts[parts.length - 1];
             String followerCount = jsonItem.getString("detail_text");

             return HotPostDataVO.builder()
                     .title(title)
                     .url(url)
                     .followerCount(Integer.parseInt(StringUtils.extractNumber(followerCount)) * 10000)
                     .build();
         }).collect(Collectors.toList());
         return dataList;
     }
}
