package com.hsu_mafia.motoo.api.presentation.execution;

import com.hsu_mafia.motoo.api.domain.execution.Execution;
import com.hsu_mafia.motoo.api.domain.execution.ExecutionMapper;
import com.hsu_mafia.motoo.api.dto.execution.ExecutionDto;
import com.hsu_mafia.motoo.api.domain.execution.ExecutionService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/executions")
@RequiredArgsConstructor
@Tag(name = "Execution", description = "체결 내역 관련 API")
public class ExecutionController {
    private final ExecutionService executionService;
    private final ExecutionMapper executionMapper;

    @GetMapping
    @Operation(summary = "내 체결 내역 조회", description = "사용자의 체결 내역을 조회합니다.")
    public ResponseEntity<CommonResponse<List<ExecutionDto>>> getExecutions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = 1L;

        Pageable pageable = PageRequest.of(page, size);

        List<Execution> executions = executionService.getExecutions(userId, pageable);
        List<ExecutionDto> executionDtos = executions.stream()
                .map(executionMapper::toExecutionDto)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(executionDtos));
    }
} 