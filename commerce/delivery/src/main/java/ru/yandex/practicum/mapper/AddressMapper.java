package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.warehouse.dto.AddressDto;

@UtilityClass
public class AddressMapper {
    public static Address mapToEntity(AddressDto addressDto) {
        return new Address(null, addressDto.country(), addressDto.city(), addressDto.street(),
                addressDto.house(), addressDto.flat());
    }

    public static AddressDto mapToDto(Address address) {
        return new AddressDto(address.getCountry(), address.getCity(), address.getStreet(),
                address.getHouse(), address.getFlat());
    }
}
