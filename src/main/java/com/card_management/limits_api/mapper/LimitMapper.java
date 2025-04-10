package com.card_management.limits_api.mapper;

import com.card_management.limits_api.dto.BalancesByLimitDto;
import com.card_management.limits_api.dto.LimitCreateDto;
import com.card_management.limits_api.dto.LimitDto;
import com.card_management.limits_api.model.Limit;
import com.card_management.users_api.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {UserMapper.class}
)
public abstract class LimitMapper {

    @Mapping(target = "user", source = "userId")
    public abstract Limit map(LimitCreateDto dto);

    @Mapping(target = "userUuid", source = "user.uuid")
    public abstract LimitDto map(Limit model);

    @Mapping(target = "userUuid", source = "user.uuid")
    public abstract BalancesByLimitDto mapBalances(Limit model);
}
