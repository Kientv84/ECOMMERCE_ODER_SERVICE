package com.ecommerce.kientv84.services.Impls;

import com.ecommerce.kientv84.dtos.requests.ShippingMethodRequest;
import com.ecommerce.kientv84.dtos.requests.ShippingMethodUpdateRequest;
import com.ecommerce.kientv84.dtos.responses.ShippingMethodResponse;
import com.ecommerce.kientv84.entities.ShippingMethodEntity;
import com.ecommerce.kientv84.exceptions.EnumError;
import com.ecommerce.kientv84.exceptions.ServiceException;
import com.ecommerce.kientv84.mappers.ShippingMethodMapper;
import com.ecommerce.kientv84.repositories.ShippingMethodRepository;
import com.ecommerce.kientv84.services.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingMethodServiceImpl implements ShippingMethodService {
    private final ShippingMethodRepository shippingMethodRepository;
    private final ShippingMethodMapper shippingMethodMapper;

    @Override
    public List<ShippingMethodResponse> getAllShippingMethod() {
        try {
             List<ShippingMethodResponse> responses = shippingMethodRepository.findAll().stream().map(e -> shippingMethodMapper.mapToShippingMethodResponse(e)).toList();

             return responses;

        } catch (Exception e) {
            throw new ServiceException(EnumError.SHIPPING_METHOD_GET_ERROR, "shipping.method.get.err");
        }
    }

    @Override
    public ShippingMethodResponse createShippingMethod(ShippingMethodRequest request) {
        try {
            ShippingMethodEntity checkMethod = shippingMethodRepository.findShippingMethodByShippingCode(request.getShippingCode());

            if ( checkMethod != null ) {
                throw new ServiceException(EnumError.SHIPPING_METHOD_DATA_EXISTED, "shipping.method.exit");
            }

            ShippingMethodEntity shippingMethod = ShippingMethodEntity.builder()
                    .shippingCode(request.getShippingCode())
                    .shippingName(request.getShippingName())
                    .description(request.getDescription())
                    .status(request.getStatus())
                    .baseFee(request.getBaseFee())
                    .build();

            shippingMethodRepository.save(shippingMethod);

            return shippingMethodMapper.mapToShippingMethodResponse(shippingMethod);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public ShippingMethodResponse updateShippingMethod(UUID id, ShippingMethodUpdateRequest updateData) {
        try {
            ShippingMethodEntity shippingMethod = shippingMethodRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.SHIPPING_METHOD_GET_ERROR, "shipping.method.get.err"));

            if ( updateData.getShippingCode() != null) {
                shippingMethod.setShippingCode(updateData.getShippingCode());
            }
            if (updateData.getShippingName() != null) {
                shippingMethod.setShippingName(updateData.getShippingName());
            }
            if (updateData.getDescription() != null) {
                shippingMethod.setDescription(updateData.getDescription());
            }
            if (updateData.getStatus() != null) {
                shippingMethod.setStatus(updateData.getStatus());
            }

            shippingMethodRepository.save(shippingMethod);

            return shippingMethodMapper.mapToShippingMethodResponse(shippingMethod);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public ShippingMethodResponse getShippingMethodById(UUID id) {
        try {
            ShippingMethodEntity shippingMethod = shippingMethodRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.SHIPPING_METHOD_GET_ERROR, "shipping.method.get.err"));

            return shippingMethodMapper.mapToShippingMethodResponse(shippingMethod);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public String deleteShippingMethod(List<UUID> ids) {
        try {
            if ( ids == null || ids.isEmpty()) {
                throw new ServiceException(EnumError.SHIPPING_METHOD_ERR_DEL_EM, "shipping.method.delete.empty");
            }

            List<ShippingMethodEntity> foundIds = shippingMethodRepository.findAllById(ids);

            System.out.println("Find collection:" + foundIds.toString());

            if ( foundIds.isEmpty()) {
                throw new ServiceException(EnumError.SHIPPING_METHOD_ERR_NOT_FOUND, "shipping.method.delete.not.found");
            }

            shippingMethodRepository.deleteAllById(ids);

            return "Deleted shipping methods successfully: {}" + ids;

        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }
}
