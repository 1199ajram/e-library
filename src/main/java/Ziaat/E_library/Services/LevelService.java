package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.LevelRequestDto;
import Ziaat.E_library.Dto.LevelResponseDto;
import Ziaat.E_library.Model.Level;
import Ziaat.E_library.Model.Program;
import Ziaat.E_library.Repository.LevelRepository;
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
public class LevelService {
    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private ProgramRepository programRepository;


    public LevelResponseDto createLevel(LevelRequestDto request) {

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found"));
        Level level = new Level();
        level.setLevelName(request.getLevelName());
        level.setLevelCode(request.getLevelCode());
        level.setIsActive(request.getIsActive());
        level.setProgram(program);
        return mapToResponse(levelRepository.save(level));
    }

    public LevelResponseDto updateLevel(UUID id, LevelRequestDto request) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        level.setProgram(program);
        level.setLevelName(request.getLevelName());
        level.setLevelCode(request.getLevelCode());
        level.setIsActive(request.getIsActive());
        return mapToResponse(levelRepository.save(level));
    }

    public void deleteLevel(UUID id) {
        levelRepository.deleteById(id);
    }

    public LevelResponseDto getLevel(UUID id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));
        return mapToResponse(level);
    }

    public Page<Level> getAllLevel(int page, int size, String sortBy, String sortDir, String search) {
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return levelRepository.searchLevel(search, pageable);
        }
        return levelRepository.findAll(pageable);
    }

    public List<Level> getActive() {
        return levelRepository.findByIsActiveTrue();
    }


    public List<Level> getActivePublic() {
        return levelRepository.getActiveLimit();
    }


    public List<LevelResponseDto> getActiveLevelsByProgram(UUID programId) {
        return levelRepository.findByProgram_ProgramIdAndIsActiveTrue(programId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private LevelResponseDto mapToResponse(Level level) {
        LevelResponseDto response = new LevelResponseDto();
        response.setLevelId(level.getLevelId());
        response.setLevelName(level.getLevelName());
        response.setLevelCode(level.getLevelCode());
        response.setIsActive(level.getIsActive());
        return response;
    }
}
