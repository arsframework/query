package wuyq.query.pojo.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 用户查询参数
 *
 * @author wuyongqiang
 * @date 2020/9/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryParam {
    /**
     * 数据源（用户对象列表JSON）
     */
    @NotNull(message = "数据源不能为空")
    private String source;

    /**
     * 过滤条件
     * a = 1 and b = 2 or (c = 3 and d = 4)
     */
    private String where;

    /**
     * 排序字段
     * a,-b,+c
     */
    private String order;

    /**
     * 分组字段
     */
    private String group;

    /**
     * 列表截取长度
     */
    private Integer limit;
}
