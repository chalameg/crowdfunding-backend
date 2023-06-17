package com.dxvalley.crowdfunding.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;

public class PaginationUtils {

    // Create a pageable object with sorting based on the given parameters.
    public static Pageable createPageableWithSorting(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }

    // Sets pagination headers based on the given Page object.
    public static <T> HttpHeaders setPaginationHeaders(Page<T> page) {
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.set("X-Page-Number", String.valueOf(page.getNumber()));
        responseHeaders.set("X-Page-Size", String.valueOf(page.getSize()));
        responseHeaders.set("X-Total-Pages", String.valueOf(page.getTotalPages()));
        responseHeaders.set("X-Total-Elements", String.valueOf(page.getTotalElements()));
        return responseHeaders;
    }
}
