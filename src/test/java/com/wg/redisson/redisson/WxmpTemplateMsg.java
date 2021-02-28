package com.wg.redisson.redisson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;
import weixin.popular.api.UserAPI;
import weixin.popular.bean.user.FollowResult;
import weixin.popular.bean.user.User;
import weixin.popular.bean.user.UserInfoList;

import java.io.IOException;
import java.util.*;

@Slf4j
public class WxmpTemplateMsg {

    private static final String appid = "xxx";

    private static final String baseurl = "https://xxx";
    private static final String tmpUrl ="http://xxx";

    private final OkHttpClient httpClient;

    private volatile static boolean syncWxUserTaskRunning = false;

    private String accessToken;


    public WxmpTemplateMsg() throws IOException{
        this.httpClient = new OkHttpClient();
        accessToken = getAccesstoken();
    }


    private String getAccesstoken() throws IOException {
        Request req = new Request.Builder().url(baseurl+"/wxAuth/getAccessToken?appid="+appid).build();
        try(Response response = httpClient.newCall(req).execute()) {
            accessToken = JSON.parseObject(response.body().string()).getString("accessToken");
            return accessToken;
        }
    }

    @Test
    public void testat() throws IOException{
        System.out.println(getAccesstoken());
    }

    @Test
    public void testsync(){
        try {
            syncWxUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void syncWxUsers() throws IOException{

        if (syncWxUserTaskRunning) {
            return;//同步较慢，防止个多线程重复执行同步任务
        }
        syncWxUserTaskRunning = true;
        log.info("同步公众号粉丝列表：任务开始");
        boolean hasMore = true;
        String nextOpenid = null;

        int page = 1;
        while (hasMore) {
            FollowResult followResult = UserAPI.userGet(accessToken, nextOpenid);
            if(!followResult.isSuccess()) {
                log.error("同步公众号粉丝出错:{},{}", followResult.getErrcode(),followResult.getErrmsg());
                followResult = UserAPI.userGet(getAccesstoken(), nextOpenid);
            }
            log.info("拉取openid列表：第{}页，数量：{}", page++, followResult.getCount());
            List<String> openids = Arrays.asList(followResult.getData().getOpenid().clone());
            this.syncWxUsers(openids);
            nextOpenid = followResult.getNext_openid();
            hasMore = !StringUtils.isEmpty(nextOpenid) && followResult.getCount() >= 10000;
        }

            //

        log.info("同步公众号粉丝列表：完成");
        syncWxUserTaskRunning = false;
    }

    public void syncWxUsers(List<String> openids)  throws IOException {
        if (openids.size() < 1) {
            return;
        }
        final String batch = openids.get(0).substring(20);//截取首个openid的一部分做批次号（打印日志时使用，无实际意义）

        int start = 0, batchSize = openids.size(), end = Math.min(100, batchSize);
        log.info("开始处理批次：{}，批次数量：{}", batch, batchSize);
        while (start < end && end <= batchSize) {//分批处理,每次最多拉取100个用户信息
            final int finalStart = start, finalEnd = end;
            final List<String> subOpenids = openids.subList(finalStart, finalEnd);
            TaskExcutor.submit(() -> {//使用线程池同步数据，否则大量粉丝数据需同步时会很慢
                log.info("同步批次:【{}--{}-{}】，数量：{}", batch, finalStart, finalEnd, subOpenids.size());
                List<User> wxMpUsers = null;//批量获取用户信息，每次最多100个

                UserInfoList userInfoList = UserAPI.userInfoBatchget(accessToken, "zh-CN", subOpenids);
                if(!userInfoList.isSuccess()){
                    log.error("同步出错，批次：【{}--{}-{}】，错误信息：{},{}", batch, finalStart, finalEnd,userInfoList.getErrcode(),userInfoList.getErrmsg());
                    try {
                        userInfoList = UserAPI.userInfoBatchget(getAccesstoken(), "zh-CN", subOpenids);
                    }catch (IOException e){
                        log.error("同步出错，批次：【{}--{}-{}】，错误信息：{}", batch, finalStart, finalEnd,e);
                    }
                }
                wxMpUsers = userInfoList.getUser_info_list();

                if (wxMpUsers != null && !wxMpUsers.isEmpty()) {
                    wxMpUsers.forEach(System.out::println);
                }
            });
            start = end;
            end = Math.min(end + 100, openids.size());
        }
        log.info("批次：{}处理完成", batch);
    }


    private void sendTmpmsg(String openid,String nickname){

        WxMpTemplateMessage msg = WxMpTemplateMessage.builder().build();
        List<WxMpTemplateData> data = new ArrayList<>();
        data.add(new WxMpTemplateData("first","日圆，月圆，元宵圆，圆圆满满。\n尊敬的客户您好，您的元宵祝福已送达！"));
        data.add(new WxMpTemplateData("keyword1","VIP客户元宵节电子贺卡"));
        data.add(new WxMpTemplateData("keyword2",nickname));
        data.add(new WxMpTemplateData("remark","元宵节后再享豪礼，惊喜福利送给你。24小时内领取！","#ff0000"));
        msg.setData(data);
        msg.setTemplateId("_8SznsTvLKajPgly7lhEMD_peuSWUIHjXWXHeCk_JhU");
        msg.setToUser(openid);
        msg.setUrl(tmpUrl);

        JSONObject obj = new JSONObject();
        obj.put("appid",appid);
        obj.put("wxMpTemplateMessage",msg);
        try {
            send(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tstsend(){
        User u = new User();
        u.setNickname("wg");
        u.setOpenid("oCGxZwuUQ-cwE4_bMtS1ESJs2Q4A");
        sendTmpmsg(u.getOpenid(),u.getNickname());
    }

    public void send(String body) throws IOException {
        System.out.println("body = " + body);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, body);
        String url = baseurl+"/wxmsgsend/wxTempMsg/"+ UUID.randomUUID().toString().replace("-","");
        System.out.println("url = " + url);
        Request req = new Request.Builder()
                .post(requestBody).url(url).build();
        String string = httpClient.newCall(req).execute().body().string();
        System.out.println("string = " + string);

    }

    @Test
    public void batch(){
        int pageNo = 1 ;
        int limit = 500;
        String body = users(1,limit);
        System.out.println("body = " + body);
        JSONObject obj = JSON.parseObject(body);
        JSONObject pd = obj.getJSONObject("page");
        int totalPage = pd.getIntValue("totalPage");
        JSONArray list = pd.getJSONArray("list");
        batchSend(list);
        list.forEach(System.out::println);
        while (pageNo < totalPage){
            pageNo++;
            body = users(pageNo,limit);
            pd = JSON.parseObject(body).getJSONObject("page");
            System.out.println("page:"+pd.getIntValue("currPage"));
            list = pd.getJSONArray("list");
            //batchSend(list);
        }
        //System.out.println("mp "+mp.size());
        //System.out.println("ct "+count);
    }
    //Map<String,String> mp = new ConcurrentHashMap<>();
    //private volatile static int count = 0;
    //ThreadPoolExecutor pool = new ThreadPoolExecutor(2,4,1, TimeUnit.MINUTES,new LinkedBlockingQueue<Runnable>());
    public void batchSend(JSONArray arr){
        arr.stream().map(x-> (JSONObject)x ).forEach(u->{
            //System.out.println("count="+count+" ,openid=" + u.getString("openid")+" ,nickname="+ u.getString("nickname"));
            //mp.put(u.getString("openid"),u.getString("nickname"));
            //count++;
            sendTmpmsg(u.getString("openid"),u.getString("nickname"));
            System.out.println(Thread.currentThread().getName() + "发送成功: openid="+u.getString("openid")+", nickname="+u.getString("nickname"));

        });
    }

    public String users(int page,int limit){
        Request req = new Request.Builder().url(baseurl+"/manage/wxUser/list?limit="+limit+"&page="+page).header("Cookie","appid="+appid).build();
        try {
            return httpClient.newCall(req).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Test
    public void testaaa(){
        String body = "{\"wxMpTemplateMessage\":{\"data\":{\"first\":{\"value\":\"日圆，月圆，元宵圆，圆圆满满。\\n尊敬的客户您好，您的元宵祝福已送达！\"},\"keyword1\":{\"value\":\"VIP客户元宵节电子贺卡\"},\"keyword2\":{\"value\":\"wg\"},\"remark\":{\"color\":\"#ff0000\",\"value\":\"元宵节后再享豪礼，惊喜福利送给你。24小时内领取！\"}},\"template_id\":\"_8SznsTvLKajPgly7lhEMD_peuSWUIHjXWXHeCk_JhU\",\"touser\":\"oCGxZwuUQ-cwE4_bMtS1ESJs2Q4A\",\"url\":\"http://sharewhact.kaisenbaoxian.com/yuanxiaojie/yuanxiaojie\"},\"appid\":\"wx67df753ba3c51172\"}";

        JSONObject obj = JSON.parseObject(body);
        String appid = obj.getString("appid");
        WxMpTemplateMessage tempMsg = obj.getObject("wxMpTemplateMessage", WxMpTemplateMessage.class);
        System.out.println("tempMsg = " + tempMsg);
    }

}
