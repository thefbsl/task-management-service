package kz.em.task.management.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedDataDto <T> {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private List<T> content;
    private int totalPages;
}