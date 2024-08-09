package kz.em.task.management.api.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {
    public static Pageable toPageable(Integer pageNumber, Integer pageSize, String sort, String order) {
        pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 0;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 20;
        Sort dataSort = Sort.unsorted();
        if (sort != null) {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
            dataSort = Sort.by(sortDirection, sort);
        }
        return PageRequest.of(pageNumber, pageSize, dataSort);
    }
}
