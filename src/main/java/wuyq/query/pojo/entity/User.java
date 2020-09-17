package wuyq.query.pojo.entity;

import java.util.Map;
import java.util.HashMap;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户数据模型
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 用户属性枚举
     */
    public enum Property {
        /**
         * 用户ID
         */
        ID("id"),

        /**
         * 用户名称
         */
        NAME("name"),

        /**
         * 联系电话
         */
        MOBILE("mobile");

        /**
         * 属性名称
         */
        public final String name;

        /**
         * 属性枚举名称/枚举影射表
         */
        private static final Map<String, Property> PROPERTIES = new HashMap<>(Property.values().length);

        static {
            for (Property property : Property.values()) {
                PROPERTIES.put(property.name, property);
            }
        }

        Property(String name) {
            this.name = name;
        }

        /**
         * 将属性名称转换成属性枚举
         *
         * @param property 属性名称
         * @return 属性枚举
         */
        public static Property parse(String property) {
            return property == null ? null : PROPERTIES.get(property);
        }
    }

    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 获取属性值
     *
     * @param property 属性名称
     * @return 属性值
     */
    public Object getPropertyValue(String property) {
        return this.getPropertyValue(Property.parse(property));
    }

    /**
     * 获取属性值
     *
     * @param property 属性枚举
     * @return 属性值
     */
    public Object getPropertyValue(Property property) {
        if (property == Property.ID) {
            return this.id;
        } else if (property == Property.NAME) {
            return this.name;
        } else if (property == Property.MOBILE) {
            return this.mobile;
        }
        return null;
    }

    /**
     * 比较属性值
     *
     * @param property 属性名称
     * @return 比较值
     */
    public int comparePropertyValue(User user, String property) {
        return this.comparePropertyValue(user, Property.parse(property));
    }

    /**
     * 比较属性值
     *
     * @param property 属性枚举
     * @return 比较值
     */
    public int comparePropertyValue(User user, Property property) {
        if (user == null) {
            return 1;
        } else if (property == Property.ID) {
            return this.id.compareTo(user.getId());
        } else if (property == Property.NAME) {
            return this.name.compareTo(user.getName());
        } else if (property == Property.MOBILE) {
            return this.mobile.compareTo(user.getMobile());
        }
        return 0;
    }

    /**
     * 匹配属性值
     *
     * @param property 属性名称
     * @param value    属性值
     * @return true/false
     */
    public boolean matchPropertyValue(String property, Object value) {
        return this.matchPropertyValue(Property.parse(property), value);
    }

    /**
     * 匹配属性值
     *
     * @param property 属性枚举
     * @param value    属性值
     * @return true/false
     */
    public boolean matchPropertyValue(Property property, Object value) {
        if (property == Property.ID) {
            return this.id == value ? true : value == null ? false : this.id == Integer.parseInt(value.toString());
        } else if (property == Property.NAME) {
            return this.name == value ? true : value == null ? false : value.toString().equals(this.name);
        } else if (property == Property.MOBILE) {
            return this.mobile == value ? true : value == null ? false : value.toString().equals(this.mobile);
        }
        return false;
    }
}
