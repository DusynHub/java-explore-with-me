package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.user.model.dto.EwmUserDto;
import ru.practicum.ewm.user.service.EwmUserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/admin/users")
@Validated
@RequiredArgsConstructor
public class AdminEwmUserController {

    private final EwmUserService ewmUserService;

    @GetMapping
    public List<EwmUserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("[Admin Controller] received a request GET /admin/users");
        return ewmUserService.getAllUsersOrUsersByIds(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EwmUserDto postUser(@RequestBody @Valid EwmUserDto ewmUserDto) {
        log.info("[Admin Controller] received a request POST /admin/users");
        return ewmUserService.saveUser(ewmUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        log.info("[Admin Controller] received a request DELETE /admin/users/{}", userId);
        long userIdLong = Long.parseLong(userId);
        ewmUserService.deleteUser(userIdLong);
    }
}
