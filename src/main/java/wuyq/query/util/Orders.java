package wuyq.query.util;

import java.util.List;
import java.util.Collections;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;
import com.google.common.collect.Lists;
import com.arsframework.annotation.Nonnull;

/**
 * 排序操作工具类
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
public abstract class Orders {
    /**
     * 排序分隔符
     */
    private static final String SEPARATOR = ",";

    /**
     * 升序排序标记
     */
    private static final char ASC_MARK = '+';

    /**
     * 降序排序标记
     */
    private static final char DESC_MARK = '-';

    /**
     * 排序类型枚举
     */
    public enum OrderType {
        /**
         * 升序排序
         */
        ASC,

        /**
         * 降序排序
         */
        DESC;
    }

    /**
     * 排序对象
     */
    @Getter
    @EqualsAndHashCode
    public static class Order {
        /**
         * 排序标识
         */
        private String key;

        /**
         * 排序类型
         */
        private OrderType type;

        @Nonnull
        public Order(String key, OrderType type) {
            this.key = key;
            this.type = type;
        }
    }

    /**
     * 将排序表达式转换成排序对象列表
     *
     * @param expression 排序表达式
     * @return 排序对象列表
     */
    public static List<Order> parse(String expression) {
        if (StringUtils.isEmpty(expression)) {
            return Collections.EMPTY_LIST;
        }
        List<Order> orders = Lists.newLinkedList();
        for (String s : expression.split(SEPARATOR)) {
            if (!(s = s.trim()).isEmpty()) {
                char c = s.charAt(0);
                OrderType type = c == DESC_MARK ? OrderType.DESC : OrderType.ASC;
                String key = c == ASC_MARK || c == DESC_MARK ? s.substring(1) : s;
                orders.add(new Order(key.trim(), type));
            }
        }
        return orders;
    }

    /**
     * 对象属性比较器
     *
     * @param <T> 比较对象类型
     */
    public interface PropertyComparator<T> {
        /**
         * 对象属性比较
         *
         * @param t1  比较对象
         * @param t2  比较对象
         * @param key 对象比较属性名称
         * @return 比较值
         */
        int compare(T t1, T t2, String key);
    }

    /**
     * 对象排序
     *
     * @param objects    对象列表
     * @param orders     排序对象列表
     * @param comparator 对象属性比较器
     * @param <T>        排序目标对象类型
     */
    public static <T> void sort(List<T> objects, List<Order> orders, PropertyComparator<T> comparator) {
        if (objects != null && !objects.isEmpty() && orders != null && !orders.isEmpty() && comparator != null) {
            Collections.sort(objects, (o1, o2) -> {
                int c = 0;
                for (Order order : orders) {
                    if ((c = comparator.compare(o1, o2, order.getKey())) != 0) {
                        return order.getType() == OrderType.ASC ? c : -c;
                    }
                }
                return c;
            });
        }
    }
}
