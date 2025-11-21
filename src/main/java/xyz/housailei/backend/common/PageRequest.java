package xyz.housailei.backend.common;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 请求封装类
 */
@Data
@Schema(description = "分页请求参数")
public class PageRequest {

    /**
     * 当前页号
     */
    @Schema(description = "当前页号", example = "1")
    private int pageNum = 1;

    /**
     * 页面大小
     */
    @Schema(description = "页面大小", example = "10")
    private int pageSize = 10;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "createdTime")
    private String sortField="created_time";

    /**
     * 排序顺序（默认降序）
     */
    @Schema(description = "排序顺序（默认降序）", example = "descend")
    private String sortOrder = "descend";

    public void setSortField(String sortField) {
        this.sortField = StringUtils.camelToUnderline(sortField);
    }

    public <T> void applyOrder(Page<T> page) {
        if (this.sortField != null && !this.sortField.isEmpty()) {
            String order = this.sortOrder;
            boolean isDesc = order == null || "descend".equalsIgnoreCase(order) || "desc".equalsIgnoreCase(order);
            page.addOrder(isDesc ? OrderItem.desc(this.sortField) : OrderItem.asc(this.sortField));
        }
    }
}