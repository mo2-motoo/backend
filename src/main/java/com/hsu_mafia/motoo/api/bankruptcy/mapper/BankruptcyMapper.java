package com.hsu_mafia.motoo.api.bankruptcy.mapper;

import com.hsu_mafia.motoo.api.bankruptcy.dto.response.AllBankruptcyRes;
import com.hsu_mafia.motoo.api.bankruptcy.dto.response.DetailBankruptcyRes;
import com.hsu_mafia.motoo.api.bankruptcy.entity.BankruptcyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BankruptcyMapper {

    BankruptcyMapper INSTANCE = Mappers.getMapper(BankruptcyMapper.class);

    AllBankruptcyRes toAllBankruptcyRes(BankruptcyEntity bankruptcy);

    DetailBankruptcyRes toDetailBankruptcyRes(BankruptcyEntity bankruptcy);
} 