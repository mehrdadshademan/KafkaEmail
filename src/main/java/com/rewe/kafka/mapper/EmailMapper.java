package com.rewe.kafka.mapper;

import com.rewe.kafka.domain.EmailModel;
import com.rewe.kafka.dto.EmailResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {
    EmailResponseDto toDto(EmailModel emailModel);

}
