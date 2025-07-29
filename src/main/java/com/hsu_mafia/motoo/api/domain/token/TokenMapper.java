package com.hsu_mafia.motoo.api.domain.token;

import com.hsu_mafia.motoo.api.dto.token.TokenRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    TokenMapper INSTANCE = Mappers.getMapper(TokenMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "expiration", source = "accessTokenTokenExpired", qualifiedByName = "stringToLocalDateTime")
    Token toToken(TokenRes tokenRes);

    @Named("stringToLocalDateTime")
    static LocalDateTime stringToLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        // 예시: "2024-07-09 12:34:56" 형식
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}
