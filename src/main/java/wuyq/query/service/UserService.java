package wuyq.query.service;

import java.util.List;

import wuyq.query.pojo.entity.User;
import wuyq.query.util.Conditions;
import wuyq.query.util.Orders;

/**
 * 用户业务操作接口
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
public interface UserService {
    /**
     * 查询用户
     *
     * @param users     用户列表数据源
     * @param condition 查询条件
     * @param orders    排序字段列表
     * @param groups    分组字段列表
     * @param limit     用户列表截取长度
     * @return
     */
    List<User> query(List<User> users, Conditions.Condition condition, List<Orders.Order> orders, List<String> groups, Integer limit);
}
