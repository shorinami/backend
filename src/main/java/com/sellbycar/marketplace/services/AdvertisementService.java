package com.sellbycar.marketplace.services;

import com.sellbycar.marketplace.models.entities.Car;
import com.sellbycar.marketplace.repositories.AdvertisementRepository;
import com.sellbycar.marketplace.models.entities.Advertisement;
import com.sellbycar.marketplace.models.entities.User;
import com.sellbycar.marketplace.models.dto.AdvertisementDTO;
import com.sellbycar.marketplace.utilities.exception.UserDataException;
import com.sellbycar.marketplace.utilities.mapper.AdvertisementMapper;
import com.sellbycar.marketplace.utilities.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final JwtUtils jwtUtils;
    private final AdvertisementRepository advertisementRepository;
    private final UserService userService;
    private final AdvertisementMapper advertisementMapper;

    private String getTokenFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    @Transactional
    public List<AdvertisementDTO> findAllAd() {
        List<Advertisement> advertisements = advertisementRepository.findAll();
        return advertisements.stream()
                .map(advertisementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdvertisementDTO getAd(Long id) {
        Optional<Advertisement> ad = advertisementRepository.findById(id);
        if (ad.isPresent()) {
            return advertisementMapper.toDTO(ad.get());
        }
        //TODO
        throw new UserDataException("ADv with ID " + id + " not found");
    }

    @Transactional
    public void saveNewAd(AdvertisementDTO advertisementDTO) {
        Advertisement advertisement = advertisementMapper.toModel(advertisementDTO);
        User user = userService.getUserFromSecurityContextHolder();
        advertisement.setUser(user);
        advertisementRepository.save(advertisement);
    }

    public AdvertisementDTO updateADv(AdvertisementDTO advertisementDTO, Long id) {
        User user = userService.getUserFromSecurityContextHolder();

        Advertisement existingAd = advertisementRepository.findById(id)
                .orElseThrow(() -> new UserDataException("Ad with ID " + id + " not found"));

        if (user.getId().equals(existingAd.getUser().getId())) {
            existingAd.setName(advertisementDTO.getName());
            existingAd.setDescription(advertisementDTO.getDescription());
            existingAd.setPrice(advertisementDTO.getPrice());
            existingAd.setChange(advertisementDTO.isChange());
            existingAd.setBargain(advertisementDTO.isBargain());
            existingAd.setCrashed(advertisementDTO.isCrashed());
            Car car = existingAd.getCar();
            car.setYearToCreate(advertisementDTO.getCarDTO().getYearToCreate());
            car.setCarNumber(advertisementDTO.getCarDTO().getCarNumber());
            car.setVinNumber(advertisementDTO.getCarDTO().getVinNumber());
            car.setMileage(advertisementDTO.getCarDTO().getMileage());
            existingAd.setCar(car);

            advertisementRepository.save(existingAd);
            return advertisementMapper.toDTO(existingAd);
        } else {
            throw new UserDataException("You don't have permission to update this ad");
        }
    }


}