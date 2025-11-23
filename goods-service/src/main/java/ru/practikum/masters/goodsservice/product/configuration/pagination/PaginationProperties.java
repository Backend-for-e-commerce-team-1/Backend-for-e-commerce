package ru.practikum.masters.goodsservice.product.configuration.pagination;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pagination")
@Data
public class PaginationProperties {
    private int defaultPageSize = 20;
    private int maxPageSize = 1000;
    private boolean allowUnpaged = false;
}
