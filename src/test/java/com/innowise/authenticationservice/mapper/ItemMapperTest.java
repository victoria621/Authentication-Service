package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.ItemRequest;
import com.innowise.authenticationservice.dto.ItemResponse;
import com.innowise.authenticationservice.entity.Item;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toEntity_ShouldMapRequestToEntity() {
        ItemRequest request = new ItemRequest("Laptop", "Gaming laptop", BigDecimal.valueOf(1200.99));

        Item entity = itemMapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Laptop");
        assertThat(entity.getDescription()).isEqualTo("Gaming laptop");
        assertThat(entity.getPrice()).isEqualTo(BigDecimal.valueOf(1200.99));
    }

    @Test
    void toDto_ShouldMapEntityToResponse() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Mouse");
        item.setDescription("Wireless mouse");
        item.setPrice(BigDecimal.valueOf(29.99));
        item.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        ItemResponse response = itemMapper.toDto(item);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Mouse");
        assertThat(response.description()).isEqualTo("Wireless mouse");
        assertThat(response.price()).isEqualTo(BigDecimal.valueOf(29.99));
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void updateEntity_ShouldUpdateExistingEntity() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Old Name");
        item.setDescription("Old Desc");
        item.setPrice(BigDecimal.valueOf(10.0));

        ItemRequest request = new ItemRequest("New Name", "New Desc", BigDecimal.valueOf(20.0));

        itemMapper.updateEntity(request, item);

        assertThat(item.getName()).isEqualTo("New Name");
        assertThat(item.getDescription()).isEqualTo("New Desc");
        assertThat(item.getPrice()).isEqualTo(BigDecimal.valueOf(20.0));
    }
}