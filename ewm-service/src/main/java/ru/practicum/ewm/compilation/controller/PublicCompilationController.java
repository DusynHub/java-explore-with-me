package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilationsFromUser(
            @RequestParam(defaultValue = "false") Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[Public Compilation Controller] received a public request GET /compilations");
        return compilationService.getCompilationsFromUser(pinned, from, size);
    }

    @GetMapping("/{compilationIdInString}")
    public CompilationDto getCompilationsFromUser(
            @PathVariable String compilationIdInString) {
        log.info("[Public Compilation Controller] received a public request GET /compilations/{}",
                compilationIdInString);
        long compilationId = Long.parseLong(compilationIdInString);
        return compilationService.getCompilationById(compilationId);
    }
}
