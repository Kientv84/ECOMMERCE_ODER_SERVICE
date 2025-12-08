package com.ecommerce.kientv84.services.Impls;

import com.ecommerce.kientv84.commons.Constant;
import com.ecommerce.kientv84.dtos.requests.ShippingMethodRequest;
import com.ecommerce.kientv84.dtos.requests.ShippingMethodUpdateRequest;
import com.ecommerce.kientv84.dtos.requests.search.shippingMethod.ShippingMethodSearchModel;
import com.ecommerce.kientv84.dtos.requests.search.shippingMethod.ShippingMethodSearchOption;
import com.ecommerce.kientv84.dtos.requests.search.shippingMethod.ShippingMethodSearchRequest;
import com.ecommerce.kientv84.dtos.responses.OrderResponse;
import com.ecommerce.kientv84.dtos.responses.PagedResponse;
import com.ecommerce.kientv84.dtos.responses.ShippingMethodResponse;
import com.ecommerce.kientv84.entities.OrderEntity;
import com.ecommerce.kientv84.entities.ShippingMethodEntity;
import com.ecommerce.kientv84.enums.OrderStatus;
import com.ecommerce.kientv84.exceptions.EnumError;
import com.ecommerce.kientv84.exceptions.ServiceException;
import com.ecommerce.kientv84.mappers.ShippingMethodMapper;
import com.ecommerce.kientv84.repositories.ShippingMethodRepository;
import com.ecommerce.kientv84.services.RedisService;
import com.ecommerce.kientv84.services.ShippingMethodService;
import com.ecommerce.kientv84.utils.PageableUtils;
import com.ecommerce.kientv84.utils.SpecificationBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingMethodServiceImpl implements ShippingMethodService {
    private final ShippingMethodRepository shippingMethodRepository;
    private final ShippingMethodMapper shippingMethodMapper;
    private final RedisService redisService;

    @Override
    public PagedResponse<ShippingMethodResponse> getAllShippingMethod(ShippingMethodSearchRequest request) {
        log.info("Listing get all shipping method api ...");

        String key = "shipping_methods:" + request.hashKey();
        try {
           // get redis
            PagedResponse<ShippingMethodResponse> cached = redisService.getValue(key, new TypeReference<PagedResponse<ShippingMethodResponse>>() {
            });

            if ( cached != null ) {
                log.info("Redis get for key: {}", key);
                return cached;
            }

            // handle get all
            ShippingMethodSearchOption option = request.getSearchOption();
            ShippingMethodSearchModel model = request.getSearchModel();

            List<String> allowedField = List.of("shippingMethodCode", "createdDate");

            PageRequest pageRequest = PageableUtils.buildPageRequest(
                    option.getPage(),
                    option.getSize(),
                    option.getSort(),
                    allowedField,
                    "createdDate",
                    Sort.Direction.DESC
            );

            Specification<ShippingMethodEntity> spec = new SpecificationBuilder<ShippingMethodEntity>()
                    .equal("status", model.getStatus())
                    .likeAnyFieldIgnoreCase(model.getQ(), "shippingMethodCode")
                    .build();

            Page<ShippingMethodResponse> result = shippingMethodRepository.findAll(spec, pageRequest).map(shippingMethodMapper::mapToShippingMethodResponse);

            PagedResponse<ShippingMethodResponse> response = new PagedResponse<>(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    result.getContent()
            );

            // add redis

            redisService.setValue(key, response, Constant.SEARCH_CACHE_TTL);

            log.info("Redis MISS, caching search result for key {}", key);

            return response;

        } catch (Exception e) {
            throw new ServiceException(EnumError.SHIPPING_METHOD_GET_ERROR, "shipping.method.get.err");
        }
    }

    @Override
    public List<ShippingMethodResponse> searchShippingMethodSuggestion(String q, int limit) {
        try {
            List<ShippingMethodResponse> responses = shippingMethodRepository.searchShippingMethodSuggestion(q, limit).stream().map(ship -> shippingMethodMapper.mapToShippingMethodResponse(ship)).toList();

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

            ShippingMethodEntity saved = shippingMethodRepository.save(shippingMethod);

            // xóa cached;
            redisService.deleteByKey("shipping_methods:*");

            return shippingMethodMapper.mapToShippingMethodResponse(saved);

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

            ShippingMethodEntity saved = shippingMethodRepository.save(shippingMethod);

            // Invalidate cache
            String key = "shipping_method:" + id;
            redisService.deleteByKey(key);

            redisService.deleteByKeys(key, "shipping_methods:list:*");

            log.info("Cache invalidated for key {}", key);

            return shippingMethodMapper.mapToShippingMethodResponse(saved);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public ShippingMethodResponse getShippingMethodById(UUID id) {
        String key = "shipping_method:" + id;

        try {
            ShippingMethodEntity shippingMethod = shippingMethodRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.SHIPPING_METHOD_GET_ERROR, "shipping.method.get.err"));

            ShippingMethodResponse response = shippingMethodMapper.mapToShippingMethodResponse(shippingMethod);
            // redis cached

            redisService.setValue(key, response, Constant.CACHE_TTL);

            return response;

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

            List<ShippingMethodEntity> shippingMethods = shippingMethodRepository.findAllById(ids);

            System.out.println("Find collection:" + shippingMethods.toString());

            if ( shippingMethods.isEmpty()) {
                throw new ServiceException(EnumError.SHIPPING_METHOD_ERR_NOT_FOUND, "shipping.method.delete.not.found");
            }

            // Soft delete:  update status
            shippingMethods.forEach(ship -> ship.setStatus(false));
            shippingMethodRepository.saveAll(shippingMethods);

            //dete cache
            ids.forEach(uuid -> redisService.deleteByKey("shipping_method:"+uuid));
            redisService.deleteByKeys("shipping_methods:*");

            log.info("Deleted shipping_methods successfully and cache invalidated: {}", ids);
            return "Deleted shipping_methods successfully: " + ids;
        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }
}
