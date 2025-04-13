package com.card_management.transaction_api.mapper;

import com.card_management.transaction_api.dto.TransactionCreateDto;
import com.card_management.transaction_api.dto.TransactionDto;
import com.card_management.transaction_api.model.Transaction;
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
public abstract class TransactionMapper {
        @Mapping(target = "user", source = "userId")
        public abstract Transaction map(TransactionCreateDto dto);

        @Mapping(target = "userUuid", source = "user.uuid")
        @Mapping(target = "maskedSource", source = "source.maskNumber")
        @Mapping(target = "maskedDestination", source = "destination.maskNumber")
        public abstract TransactionDto map(Transaction model);
}
