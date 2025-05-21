package com.QueroTrabalhar.Util;

import org.modelmapper.ModelMapper;

public class EntityDtoConverter {

    public static <D, E> D convertToDTO(E entity, Class<D> dtoClass) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(entity, dtoClass);
    }

    public static <D, E> E convertToEntity(D dto, Class<E> entityClass) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, entityClass);
    }
}
