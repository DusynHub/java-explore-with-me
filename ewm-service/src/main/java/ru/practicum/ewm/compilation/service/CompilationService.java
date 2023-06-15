package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;

public interface CompilationService {

    /**
     * Method to save new compilation
     *
     * @param newCompilationDto newCompilationDto
     * @return saved compilation
     */
    CompilationDto postCompilation (NewCompilationDto newCompilationDto);
}
