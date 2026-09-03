package pl.rkuba.drivinglicencetest.model.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T> (
    List<T> content,
    PageInfo pageInfo
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            new PageInfo(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
            )
        );
    }
}