package com.sellbycar.marketplace.rest.mapper;

import com.sellbycar.marketplace.persistance.model.Advertisement;
import com.sellbycar.marketplace.rest.dto.AdvertisementDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR
        , uses = {CarMapper.class})
public interface AdvertisementMapper {


    @Mapping(source = "carDTO", target = "car")
    Advertisement toModel(AdvertisementDTO dto);

    @Mapping(source = "car", target = "carDTO")
    AdvertisementDTO toDTO(Advertisement model);
}
