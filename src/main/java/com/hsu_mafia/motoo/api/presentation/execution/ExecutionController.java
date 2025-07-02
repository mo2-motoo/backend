package com.hsu_mafia.motoo.api.presentation.execution;

import com.hsu_mafia.motoo.api.dto.execution.ExecutionDto;
import com.hsu_mafia.motoo.api.domain.execution.ExecutionService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/executions")
@RequiredArgsConstructor
@Tag(name = "Execution", description = "체결 내역 관련 API")
public class ExecutionController {
    private final ExecutionService executionService;

    @GetMapping
    @Operation(summary = "내 체결 내역 조회", description = "사용자의 체결 내역을 조회합니다.")
    public CommonResponse<List<ExecutionDto>> getExecutions() {
        Long userId = 1L;
        return CommonResponse.success(executionService.getExecutions(userId));
    }
} 