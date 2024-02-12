package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
@SuperBuilder
public class GetItemRequestParam {
    public static Integer from;
    public static Integer size;

    public static Pageable pageRequest(Integer from, Integer size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }
}
