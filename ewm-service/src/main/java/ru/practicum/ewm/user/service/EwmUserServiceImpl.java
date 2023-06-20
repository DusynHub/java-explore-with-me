package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserMapper;
import ru.practicum.ewm.user.repository.EwmUserRepository;
import ru.practicum.ewm.util.OffsetPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EwmUserServiceImpl implements EwmUserService {

    private final EwmUserRepository ewmUserRepository;

    private final EwmUserMapper ewmUserMapper;

    @Override
    public EwmUser getEwmUserEntityById(long ewmUserId) {
        log.info("[Ewm Service] received a request to get user entity");
        return ewmUserRepository.findById(ewmUserId).orElseThrow(() ->
            new ResourceNotFoundException(
                    String.format("User with id = '%d' not found", ewmUserId)
            )
        );
    }

    @Override
    public EwmUser getEwmUserProxyById(long ewmUserId) {
        log.info("[Ewm Service] received a request to get user proxy");
        return ewmUserRepository.getReferenceById(ewmUserId);
    }

    @Override
    public boolean checkUserExistence(long userId) {
        log.info("[Ewm Service] received a request to check user existence by id");
        return ewmUserRepository.existsById(userId);
    }

    @Override
    public List<EwmUserDto> getAllUsersOrUsersByIds(List<Long> ewmUserIds,
                                                    Integer from,
                                                    Integer size) {
        log.info("[Ewm User Service] received a request to get all users");
        if (ewmUserIds != null) {
            return ewmUserRepository.findAllByIdIn(ewmUserIds).stream()
                    .map(ewmUserMapper::ewmUserToEwmUserDto)
                    .collect(Collectors.toList());
        } else {
            OffsetPageRequest pageRequest = OffsetPageRequest.of(from,
                    size,
                    Sort.by(Sort.Direction.ASC, "id")
            );
            return ewmUserRepository.findAllBy(pageRequest).stream()
                    .map(ewmUserMapper::ewmUserToEwmUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public EwmUserDto saveUser(EwmUserDto ewmUserDto) {
        log.info("[Ewm User Service] received an admin request to save user");
        return ewmUserMapper.ewmUserToEwmUserDto(
            ewmUserRepository.save(
                    ewmUserMapper.ewmUserDtoToEwnUserDto(ewmUserDto)
            )
        );
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        log.info("[Ewm User Service] received an admin request to delete user with id = '{}'", userId);
        if (!ewmUserRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    String.format("User with id = '%d' not found", userId)
            );
        }
        ewmUserRepository.deleteById(userId);
    }

    @Override
    public List<EwmShortUserDto> findAllBy(List<Long> userIds) {
        log.info("[Ewm User Service] received a request to find users by ids'");
        return ewmUserRepository.findAllById(userIds)
                .stream()
                .map(ewmUserMapper::ewmUserToEwmShortUserDto)
                .collect(Collectors.toList());
    }
}
