package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.CardResponse;
import com.innowise.authenticationservice.entity.Card;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardResponse toDto(Card card);
    List<CardResponse> toDtoList(List<Card> cards);
}
