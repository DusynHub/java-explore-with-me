package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.dto.PatchCompilationDto;

import java.util.List;

public interface CompilationService {

    /**
     * Method to save new compilation
     *
     * @param newCompilationDto newCompilationDto
     * @return saved compilation
     */
    CompilationDto postCompilation (NewCompilationDto newCompilationDto);

    /**
     * Method to delete compilation by id
     *
     * @param compilationId id of compilation to be deleted
     */
    void deleteCompilationById(long compilationId);

    /**
     * Method to patch compilation by id
     *
     * @param compilationId id of compilation to be updated
     * @param patchCompilationDto new compilation fields
     * @return updated compilation
     */
    CompilationDto patchCompilationById(long compilationId, PatchCompilationDto patchCompilationDto);

    /**
     * Method to get pinned or not compilations
     *
     * @param pinned compilation pinned status
     * @param from first compilation in result
     * @param size page size
     * @return required compilations
     */
    List<CompilationDto> getCompilationsFromUser(Boolean pinned, int from, int size);

    /**
     * Method to get compilation by id
     *
     * @param compilationId id of required compilation
     * @return required compilation
     */
    CompilationDto getCompilationById(long compilationId);
}
