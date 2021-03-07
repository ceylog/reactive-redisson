package com.wg.redisson.redisson;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.wg.redisson.redisson.entity.vo.WxUser;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class WxTemplateMsgTest {

    private AtomicLong count = new AtomicLong(0);

    private OkHttpClient client;

    public WxTemplateMsgTest(){
        this.client = new OkHttpClient.Builder().build();
    }


    @Test
    public void read(){

        AnalysisEventListener<WxUser> listener = new AnalysisEventListener<>(){

            @Override
            public void invoke(WxUser wxUser, AnalysisContext analysisContext) {
                TaskExcutor.submit(()->{
                    try {
                        String send = send(wxUser);
                        System.out.println(count.incrementAndGet()+"-"+Thread.currentThread().getName()+"-"+wxUser+"-"+send);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        };

        EasyExcel.read("/Users/sam/Downloads/微信关注用户_20210307151624.xlsx",WxUser.class,listener).sheet().doRead();
    }

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private String send(WxUser u) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(u));
        Request req = new Request.Builder().url("http://localhost:8080/sendTmsg").post(body).build();
        return client.newCall(req).execute().body().string();
    }
}
