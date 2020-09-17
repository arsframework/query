package wuyq.query.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wuyq.query.pojo.entity.User;
import wuyq.query.pojo.param.UserQueryParam;
import wuyq.query.service.UserService;
import wuyq.query.util.Conditions;
import wuyq.query.util.Orders;

/**
 * 用户接口
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 查询用户
     *
     * @param param 查询参数
     * @return 用户列表
     */
    @PostMapping("/user/query")
    public List<User> query(@RequestBody @Valid UserQueryParam param) {
        // 解析条件参数
        Conditions.Condition condition = Conditions.parse(param.getWhere());

        // 解析排序参数
        List<Orders.Order> orders = Orders.parse(param.getOrder());
        if (orders != null && !orders.isEmpty()) {
            orders = orders.stream().distinct().collect(Collectors.toList());
        }

        // 解析分组参数
        List<String> groups = StringUtils.isEmpty(param.getGroup()) ?
                Collections.emptyList() : Lists.newArrayList(param.getGroup().split(","));
        if (groups != null && !groups.isEmpty()) {
            groups = groups.stream().distinct().collect(Collectors.toList());
        }

        // 过滤用户列表
        List<User> users = JSONObject.parseArray(param.getSource(), User.class);
        return this.userService.query(users, condition, orders, groups, param.getLimit());
    }
}
