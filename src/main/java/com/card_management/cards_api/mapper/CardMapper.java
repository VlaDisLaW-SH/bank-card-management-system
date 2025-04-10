package com.card_management.cards_api.mapper;

import com.card_management.cards_api.dto.CardCreateDto;
import com.card_management.cards_api.dto.CardDto;
import com.card_management.cards_api.dto.CardNumberDto;
import com.card_management.cards_api.model.Card;
import com.card_management.technical.util.CardUtils;
import com.card_management.users_api.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {UserMapper.class,
                CardUtils.class
        }
)
public abstract class CardMapper {

    @Mapping(target = "owner", source = "ownerId")
    @Mapping(source = "cardNumber", target = "maskNumber", qualifiedByName = "maskCardNumber")
    public abstract Card map(CardCreateDto dto);

    @Mapping(target = "ownerUuid", source = "owner.uuid")
    public abstract CardDto map(Card model);

    //public abstract void update(CardUpdateDto dto, @MappingTarget Card model);

    public abstract CardNumberDto map(String cardNumber);
}
