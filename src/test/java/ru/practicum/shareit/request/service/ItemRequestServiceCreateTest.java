package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepositoryJpa;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceCreateTest {

    @Mock
    UserRepositoryJpa mockUserRepositoryJpa;
    @Mock
    ItemRequestMapper itemRequestMapper;
    @Mock
    ItemRequestRepositoryJPA mockitemRequestRepositoryJPA;
    @Mock
    ItemRepositoryJpa itemRepositoryJpa;
    @Mock
    ItemMapper itemMapper;
    ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("test").build();

    @Test
    void testCreateItemRequestException() {
        ItemRequestService itemRequestService = new ItemRequestService(itemRequestMapper, mockUserRepositoryJpa,
                mockitemRequestRepositoryJPA, itemRepositoryJpa, itemMapper);
        Mockito.when(mockUserRepositoryJpa.findById(Mockito.anyLong())).thenThrow(new DataNotFoundException("User not found"));
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, () -> itemRequestService.createItemRequest(1L, itemRequestDto)
        );

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void  testFindItemRequestIdException() {
        ItemRequestService itemRequestService = new ItemRequestService(itemRequestMapper, mockUserRepositoryJpa,
                mockitemRequestRepositoryJPA, itemRepositoryJpa, itemMapper);

       User user = new User();
       Mockito.when(mockUserRepositoryJpa.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
       Mockito.when(mockitemRequestRepositoryJPA.findById(Mockito.anyLong())).thenThrow(new DataNotFoundException("ItemRequest not found"));
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, () -> itemRequestService.findItemRequestId(1L, 2L)
        );

        Assertions.assertEquals("ItemRequest not found", exception.getMessage());
    }
}

