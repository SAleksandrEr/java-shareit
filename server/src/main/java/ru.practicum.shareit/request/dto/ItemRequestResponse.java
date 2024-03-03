package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class ItemRequestResponse {

    private Long id;

    private String description;

    private Long requestor;

    private String created;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor(force = true)
    public static class ItemRequestResponseItems {
        private Long id;

        private String description;

        private Long requestor;

        private String created;

        private List<ItemDtoResponse.ItemDtoResponseRequest> items;
    }
}
