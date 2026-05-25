package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.ItemRequest;
import com.innowise.authenticationservice.dto.ItemResponse;
import com.innowise.authenticationservice.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toEntity(ItemRequest request);

    ItemResponse toDto(Item item);

    List<ItemResponse> toDtoList(List<Item> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ItemRequest request, @MappingTarget Item item);
}