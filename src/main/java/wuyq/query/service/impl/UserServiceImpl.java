package wuyq.query.service.impl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.common.base.Objects;
import org.springframework.stereotype.Service;

import wuyq.query.pojo.entity.User;
import wuyq.query.service.UserService;
import wuyq.query.util.Conditions;
import wuyq.query.util.Orders;

/**
 * 用户业务操作实现
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public List<User> query(List<User> users, Conditions.Condition condition, List<Orders.Order> orders, List<String> groups, Integer limit) {
        // 根据过滤条件筛选用户列表
        List<User> list = Conditions.filter(users, condition, (u, m) -> u.matchPropertyValue(m.getKey(), m.getValue()));
        if (list == null || list.isEmpty()) {
            return list;
        }

        // 根据分组字段对用户列表进行分组
        if (groups != null && !groups.isEmpty()) {
            Object[] properties = new Object[groups.size()];
            Map<Integer, User> userHashMappings = new HashMap<>(list.size());
            list.forEach(u -> {
                for (int i = 0; i < groups.size(); i++) {
                    properties[i] = u.getPropertyValue(groups.get(i));
                }
                userHashMappings.put(Objects.hashCode(properties), u);
            });
            list = userHashMappings.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
        }


        // 将用户列表排序
        if (orders != null && !orders.isEmpty()) {
            Orders.sort(list, orders, (u1, u2, key) -> u1.comparePropertyValue(u2, key));
        }

        // 根据limit参数对用户列表截断
        if (limit != null && (limit = Math.min(limit, list.size())) > 0) {
            list = list.subList(0, limit);
        }
        return list;
    }

}
