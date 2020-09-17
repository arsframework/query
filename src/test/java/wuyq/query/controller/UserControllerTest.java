package wuyq.query.controller;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import wuyq.query.pojo.entity.User;
import wuyq.query.pojo.param.UserQueryParam;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户接口单元测试
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class UserControllerTest {
    /**
     * 用户数据源列表
     */
    private static final List<User> SOURCES = Lists.newArrayList(
            User.builder().id(1).name("zhangsan").mobile("18300000001").build(),
            User.builder().id(2).name("lisi").mobile("18300000001").build(),
            User.builder().id(3).name("wangwu").mobile("18300000002").build(),
            User.builder().id(1).name("zhangsan").mobile("18300000001").build()
    );

    @Resource
    private MockMvc mvc;

    /**
     * 用户查询
     *
     * @param param 查询参数
     * @return 用户列表
     */
    private List<User> query(UserQueryParam param) throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/user/query");
        request.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        request.content(JSONObject.toJSONString(param));

        MvcResult result = this.mvc.perform(request).andExpect(status().isOk()).andReturn();
        String response = result.getResponse().getContentAsString();
        return JSONObject.parseArray(response, User.class);
    }

    @Test
    public void query() throws Exception {
        UserQueryParam param = UserQueryParam.builder().source(JSONObject.toJSONString(SOURCES))
                .where("(id=1 or id = 2 or id = 3 or id = 4) and (mobile = 18300000001 or name=wangwu)")
                .order("-id,-mobile,name").group("id,name,mobile").limit(2).build();
        List<User> users = query(param);
        Assert.assertTrue(users != null && !users.isEmpty() && users.size() == 2);
        Assert.assertTrue(users.get(0).getId().equals(3));
        Assert.assertTrue(users.get(1).getId().equals(2));

        param.setGroup(null);
        param.setLimit(null);
        users = query(param);
        Assert.assertTrue(users != null && users.size() == SOURCES.size());

        param.setWhere("name = [zhangsan,lisi]");
        param.setGroup("id,name,mobile");
        users = query(param);
        Assert.assertTrue(users != null && users.isEmpty());
    }
}
