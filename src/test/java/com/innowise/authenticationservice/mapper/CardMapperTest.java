package com.innowise.authenticationservice.mapper;

import com.innowise.authenticationservice.dto.CardResponse;
import com.innowise.authenticationservice.entity.Card;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class CardMapperTest {

    private final CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

    @Test
    void toDto_ShouldMapCardToCardResponse() {
        Card card = new Card();
        card.setId(1L);
        card.setUserId(10L);
        card.setCardNumber("1234-5678-9012-3456");
        card.setCardHolderName("John Doe");
        card.setExpiryDate("12/28");

        CardResponse response = cardMapper.toDto(card);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(10L);
        assertThat(response.cardNumber()).isEqualTo("1234-5678-9012-3456");
        assertThat(response.cardHolderName()).isEqualTo("John Doe");
        assertThat(response.expiryDate()).isEqualTo("12/28");
    }

    @Test
    void toDtoList_ShouldMapCardListToCardResponseList() {
        Card card1 = new Card();
        card1.setId(1L);
        Card card2 = new Card();
        card2.setId(2L);

        var responses = cardMapper.toDtoList(java.util.List.of(card1, card2));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(1).id()).isEqualTo(2L);
    }
}