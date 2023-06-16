package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.dto.PatchCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(
            @RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("[Admin Compilation Controller] received an admin request POST /admin/compilations");
        return compilationService.postCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compilationIdInString}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(
            @PathVariable String compilationIdInString) {
        log.info("[Admin Compilation Controller] received an admin request DELETE /admin/compilations/{}",
                compilationIdInString);
        long compilationId = Long.parseLong(compilationIdInString);
        compilationService.deleteCompilationById(compilationId);
    }

    @PatchMapping("/{compilationIdInString}")
    public CompilationDto patchCompilation(
            @PathVariable String compilationIdInString,
            @RequestBody @Valid PatchCompilationDto patchCompilationDto) {
        log.info("[Admin Compilation Controller] received an admin request PATCH /admin/compilations/{}",
                compilationIdInString);
        long compilationId = Long.parseLong(compilationIdInString);
        return compilationService.patchCompilationById(compilationId, patchCompilationDto);
    }

}
