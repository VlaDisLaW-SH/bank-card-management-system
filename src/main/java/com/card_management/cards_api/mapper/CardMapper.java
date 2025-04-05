package com.card_management.cards_api.mapper;

import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.model.Card;
import com.card_management.users_api.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {UserMapper.class
        }
)
public abstract class CardMapper {

    @Mapping(target = "owner", source = "ownerId")
    //@Mapping(target = "transactions", ignore = true)
    public abstract Card map(CardCreateDto dto);

    @Mapping(target = "owner", source = "owner")
    //@Mapping(target = "transactionsInfo",source = "transactions")
    public abstract CardDto map(Card model);

    //public abstract void update(CardUpdateDto dto, @MappingTarget Card model);
}
