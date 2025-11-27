package Ziaat.E_library.Services;


import Ziaat.E_library.Dto.ProgramRequestDto;
import Ziaat.E_library.Dto.ProgramResponseDto;
import Ziaat.E_library.Model.Category;
import Ziaat.E_library.Model.Program;
import Ziaat.E_library.Repository.CategoryRepository;
import Ziaat.E_library.Repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProgramsServices {
    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public ProgramResponseDto createProgram(ProgramRequestDto request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Program program = new Program();
        program.setCategory(category);
        program.setProgramName(request.getProgramName());
        program.setProgramCode(request.getProgramCode());
        program.setIsActive(request.getIsActive());
        return mapToResponse(programRepository.save(program));
    }

    public ProgramResponseDto updateProgram(UUID id, ProgramRequestDto request) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        program.setCategory(category);
        program.setProgramName(request.getProgramName());
        program.setProgramCode(request.getProgramCode());
        program.setIsActive(request.getIsActive());
        return mapToResponse(programRepository.save(program));
    }

    public void deleteProgram(UUID id) {
        programRepository.deleteById(id);
    }

    public ProgramResponseDto getProgram(UUID id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));
        return mapToResponse(program);
    }


//    public List<Program> getActiveProgramByCategory(UUID categoryId) {
//        return programRepository.findByCategory_CategoryIdAndIsActiveTrue(categoryId);
//    }

    public List<ProgramResponseDto> getActiveProgramByCategory(UUID categoryId) {
        return programRepository.findByCategory_CategoryIdAndIsActiveTrue(categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public Page<Program> getAllPrograms(int page, int size, String sortBy, String sortDir, String search) {
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return programRepository.searchProgram(search, pageable);
        }
        return programRepository.findAll(pageable);
    }

    public List<Program> getActive() {
        return programRepository.findByIsActiveTrue();
    }

    private ProgramResponseDto mapToResponse(Program program) {
        ProgramResponseDto response = new ProgramResponseDto();
        response.setProgramId(program.getProgramId());
        response.setProgramName(program.getProgramName());
        response.setProgramCode(program.getProgramCode());
        response.setIsActive(program.getIsActive());
        return response;
    }
}
