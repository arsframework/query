package wuyq.query.util;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import org.springframework.util.StringUtils;
import com.google.common.collect.Lists;
import com.arsframework.annotation.Nonempty;
import com.arsframework.annotation.Nonnull;

/**
 * 条件处理工具类
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
public abstract class Conditions {
    /**
     * 字符串列表正则表达式匹配模式
     */
    private static final Pattern LIST_PATTERN = Pattern.compile(" *\\[.*\\] *");

    /**
     * 条件接口
     */
    public interface Condition {

    }

    /**
     * 条件包装器抽象实现
     */
    @Getter
    public static abstract class AbstractConditionWrapper implements Condition {
        private final List<Condition> conditions = Lists.newLinkedList(); // 条件集合

        @Nonempty
        public AbstractConditionWrapper(Condition... conditions) {
            for (Condition condition : conditions) {
                this.conditions.add(condition);
            }
        }

        @Nonnull
        public void addCondition(Condition condition) {
            this.conditions.add(condition);
        }

    }

    /**
     * 或逻辑实现
     */
    public static class Or extends AbstractConditionWrapper {
        /**
         * 分割符号
         */
        public static final String SEPARATOR = " or ";

        public Or(Condition... conditions) {
            super(conditions);
        }
    }

    /**
     * 与逻辑实现
     */
    public static class And extends AbstractConditionWrapper {
        /**
         * 分割符号
         */
        public static final String SEPARATOR = " and ";

        public And(Condition... conditions) {
            super(conditions);
        }

    }

    /**
     * 条件匹配逻辑实现
     */
    @Getter
    public static class Match implements Condition {
        /**
         * 匹配标识
         */
        private String key;

        /**
         * 匹配值
         */
        private Object value;

        public Match(@Nonempty String key, Object value) {
            this.key = key;
            this.value = value;
        }

    }

    /**
     * 将字符串转换成列表对象
     *
     * @param source 源字符串
     * @return 对象列表
     */
    private static List<?> parseList(CharSequence source) {
        int skip = 0;
        StringBuilder buffer = new StringBuilder();
        List<StringBuilder> buffers = Lists.newLinkedList();
        for (int i = 1; i < source.length() - 1; i++) {
            char c = source.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            } else if (c == ',' && skip == 0) {
                buffers.add(buffer);
                buffer = new StringBuilder();
            } else {
                buffer.append(c);
                if (c == '[') {
                    skip++;
                } else if (c == ']') {
                    skip--;
                }
            }
        }
        buffers.add(buffer);
        List<Object> list = new ArrayList<>(buffers.size());
        for (StringBuilder b : buffers) {
            list.add(b.length() == 0 ? null : LIST_PATTERN.matcher(b).matches() ? parseList(b) : b.toString());
        }
        return list;
    }

    /**
     * 条件表达式逻辑对象转换
     *
     * @param expression 条件表达式
     * @return 条件逻辑对象
     */
    public static Condition parse(String expression) {
        if (StringUtils.isEmpty(expression)) {
            return null;
        }
        boolean continued = false;
        int offset = 0, start = 0, end = 0;
        List<String> sections = Lists.newLinkedList();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                if (start == end) {
                    offset = i;
                }
                start++;
            } else if (c == ')') {
                if (start == ++end) {
                    sections.add(expression.substring(offset, i + 1));
                    start = 0;
                    end = 0;
                    offset = i + 1;
                    continued = true;
                }
            } else if (start == end) {
                int index;
                String handle;
                if ((i > 3 && (handle = expression.substring(index = i - 3, i + 1).toLowerCase()).equals(Or.SEPARATOR))
                        || (i > 4 && (handle = expression.substring(index = i - 4, i + 1).toLowerCase()).equals(And.SEPARATOR))) {
                    if (!continued) {
                        sections.add(expression.substring(offset, index));
                    }
                    offset = i + 1;
                    continued = false;
                    sections.add(handle);
                }
            }
        }
        if (offset < expression.length()) {
            sections.add(expression.substring(offset));
        }

        Condition condition = null;
        for (int i = 0; i < sections.size(); i += 2) {
            Condition _condition;
            String section = sections.get(i).trim();
            if (section.isEmpty()) {
                continue;
            }
            if (section.charAt(0) == '(' && section.charAt(section.length() - 1) == ')') {
                section = section.substring(1, section.length() - 1).trim();
                if (section.isEmpty()) {
                    continue;
                }
                _condition = parse(section);
            } else {
                int split = section.indexOf("=");
                String key = split < 1 ? null : section.substring(0, split).trim();
                if (StringUtils.isEmpty(key)) {
                    throw new IllegalArgumentException("Invalid expression: " + expression);
                }
                String value = section.substring(split + 1).trim();
                _condition = new Match(key, value.isEmpty() ? null : LIST_PATTERN.matcher(value).matches() ? parseList(value) : value);
            }
            if (condition == null) {
                condition = _condition;
            } else if (sections.get(i - 1).equals(Or.SEPARATOR)) {
                if (condition instanceof Or) {
                    ((Or) condition).addCondition(_condition);
                } else {
                    condition = new Or(condition, _condition);
                }
            } else {
                if (condition instanceof And) {
                    ((And) condition).addCondition(_condition);
                } else {
                    condition = new And(condition, _condition);
                }
            }
        }
        return condition;
    }

    /**
     * 条件匹配器
     *
     * @param <T> 匹配对象类型
     */
    public interface Matcher<T> {
        /**
         * 条件匹配
         *
         * @param object 匹配对象
         * @param match  匹配条件
         * @return true/false
         */
        boolean matches(T object, Match match);
    }

    /**
     * 条件匹配
     *
     * @param object    匹配对象
     * @param condition 匹配条件
     * @param matcher   匹配器对象
     * @param <T>       匹配对象类型
     * @return true/false
     */
    public static <T> boolean matches(T object, Condition condition, Matcher<T> matcher) {
        if (object == null || matcher == null) {
            return false;
        }
        if (condition instanceof Match) {
            return matcher.matches(object, (Match) condition);
        } else if (condition instanceof Or) {
            return ((Or) condition).getConditions().stream().anyMatch(c -> matches(object, c, matcher));
        } else if (condition instanceof And) {
            return ((And) condition).getConditions().stream().allMatch(c -> matches(object, c, matcher));
        }
        return false;
    }

    /**
     * 条件过滤
     *
     * @param objects   目标对象列表
     * @param condition 匹配条件
     * @param matcher   条件匹配器
     * @param <T>       匹配对象类型
     * @return 匹配结果对象列表
     */
    public static <T> List<T> filter(List<T> objects, Condition condition, Matcher<T> matcher) {
        if (objects == null || objects.isEmpty() || condition == null) {
            return objects;
        }
        // 定义是否采用多线程过滤的列表大小临界值
        int critical = 100000;

        // 过滤数据列表
        Stream<T> stream = objects.size() > critical ? objects.parallelStream() : objects.stream();
        return stream.filter(o -> matches(o, condition, matcher)).collect(Collectors.toList());
    }
}
