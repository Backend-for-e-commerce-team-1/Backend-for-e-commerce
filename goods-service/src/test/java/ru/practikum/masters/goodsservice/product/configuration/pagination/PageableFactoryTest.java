package ru.practikum.masters.goodsservice.product.configuration.pagination;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practikum.masters.goodsservice.common.exception.ValidationException;
import ru.practikum.masters.goodsservice.product.dto.ProductFilterRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageableFactoryTest {

    private PageableFactory factoryWithDefaults() {
        PaginationProperties props = new PaginationProperties();
        props.setDefaultPageSize(20);
        props.setMaxPageSize(1000);
        return new PageableFactory(props);
    }

    @Test
    void createFromFilter_pageAndAliasSort_returnsPageable() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setPage(2);
        filter.setLimit(50);
        filter.setSort("price_desc");

        Pageable p = factory.createFromFilter(filter);
        assertThat(p.getPageNumber()).isEqualTo(1);
        assertThat(p.getPageSize()).isEqualTo(50);
        Sort.Order priceOrder = p.getSort().getOrderFor("price");
        assertThat(priceOrder).isNotNull();
        assertThat(priceOrder.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void createFromFilter_offsetOnly_usesDefaultLimitAndCalculatesPageNumber() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setPage(null);
        filter.setOffset(40);

        Pageable p = factory.createFromFilter(filter);
        assertThat(p.getPageNumber()).isEqualTo(2);
        assertThat(p.getPageSize()).isEqualTo(20);
        assertThat(p.getSort().isSorted()).isFalse();
    }

    @Test
    void createFromFilter_limitOverMax_clampedToMax() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setLimit(5000);

        Pageable p = factory.createFromFilter(filter);
        assertThat(p.getPageSize()).isEqualTo(1000);
    }

    @Test
    void createFromFilter_sortExpressions_parsedCorrectly() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setPage(1);
        filter.setLimit(10);
        filter.setSort("name,asc;code,desc");

        Pageable p = factory.createFromFilter(filter);
        Sort sort = p.getSort();
        Sort.Order nameOrder = sort.getOrderFor("name");
        Sort.Order codeOrder = sort.getOrderFor("code");
        assertThat(nameOrder).isNotNull();
        assertThat(nameOrder.getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(codeOrder).isNotNull();
        assertThat(codeOrder.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void createFromFilter_invalidSortExpression_throwsValidation() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setLimit(10);
        filter.setSort("name,asc,extra");

        assertThatThrownBy(() -> factory.createFromFilter(filter))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort expression");
    }

    @Test
    void createFromFilter_invalidDirection_throwsValidation() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setLimit(10);
        filter.setSort("name,up");

        assertThatThrownBy(() -> factory.createFromFilter(filter))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort direction");
    }

    @Test
    void createFromFilter_negativeOffset_throwsValidation() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setOffset(-1);
        filter.setLimit(10);

        assertThatThrownBy(() -> factory.createFromFilter(filter))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Offset must be non-negative");
    }

    @Test
    void createFromFilter_zeroLimit_throwsValidation() {
        PageableFactory factory = factoryWithDefaults();
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setLimit(0);

        assertThatThrownBy(() -> factory.createFromFilter(filter))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Limit must be greater than 0");
    }
}