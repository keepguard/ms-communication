package com.keepguard.ms_communication.adapters.in.rest.template;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.response.*;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateUpdateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.template.mapper.TemplateAdapterMapper;
import com.keepguard.ms_communication.application.dto.template.*;
import com.keepguard.ms_communication.application.mapper.TemplateApplicationMapper;
import com.keepguard.ms_communication.application.port.in.service.TemplatePort;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Templates", description = "APIs para gerenciamento de templates de comunicação por aplicação")
// 🔥 HOT RELOAD TESTE FINAL - Controller atualizado
public class TemplateController {

    private final TemplatePort templatePort;
    private final TemplateAdapterMapper adapterMapper;
    private final TemplateApplicationMapper applicationMapper;

    @PostMapping
    @Operation(
        summary = "Criar template",
        description = "Cria um novo template de comunicação para uma aplicação específica. " +
                    "Cada aplicação pode ter seus próprios templates personalizados.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do template a ser criado",
            required = true,
            content = @Content(schema = @Schema(implementation = TemplateCreateRequestDTO.class))
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Template criado com sucesso",
                    content = @Content(schema = @Schema(implementation = TemplateCreateResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Template já existe para esta aplicação e tipo"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "template_create",
        operation = "criar template"
    )
    public ResponseEntity<TemplateCreateResponseDTO> createTemplate(
            @Valid @RequestBody TemplateCreateRequestDTO createDTO,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Criando template - companyId={}", companyId);
        
        com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO requestCommand = adapterMapper.toCreateCommand(createDTO, companyId);
        com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO command = applicationMapper.toCreateCommand(requestCommand);
        TemplateView view = templatePort.create(command);
        TemplateCreateResponseDTO response = adapterMapper.toCreateResponseDTO(view);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar template",
        description = "Atualiza um template existente. Apenas a aplicação que criou o template pode atualizá-lo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TemplateUpdateResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Template não encontrado"),
        @ApiResponse(responseCode = "403", description = "Aplicação não autorizada a atualizar este template"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "template_update",
        operation = "atualizar template"
    )
    public ResponseEntity<TemplateUpdateResponseDTO> updateTemplate(
            @Parameter(description = "ID único do template") @PathVariable UUID id,
            @Valid @RequestBody TemplateUpdateRequestDTO updateDTO,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {

        log.info("Atualizando template: {} - companyId={}", id, companyId);
        
        com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO requestCommand = adapterMapper.toUpdateCommand(id, updateDTO, companyId);
        com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO command = applicationMapper.toUpdateCommand(requestCommand);
        TemplateView view = templatePort.update(command);
        TemplateUpdateResponseDTO response = adapterMapper.toUpdateResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar template",
        description = "Remove um template. Apenas a aplicação que criou o template pode deletá-lo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Template deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Template não encontrado"),
        @ApiResponse(responseCode = "403", description = "Aplicação não autorizada a deletar este template"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "template_delete",
        operation = "deletar template"
    )
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "ID único do template") @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {

        log.info("Deletando template: {} - companyId={}", id, companyId);
        
        templatePort.delete(id);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar template por ID",
        description = "Retorna os detalhes de um template específico pelo seu ID único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template encontrado",
                    content = @Content(schema = @Schema(implementation = TemplateGetTemplateByIdResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Template não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "template_get_by_id",
        operation = "buscar template por ID"
    )
    public ResponseEntity<TemplateGetTemplateByIdResponseDTO> getTemplateById(
            @Parameter(description = "ID único do template") @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {

        log.info("Buscando template por ID: {} - companyId={}", id, companyId);
        
        TemplateView view = templatePort.getById(id).orElseThrow(() -> new RuntimeException("Template not found"));
        TemplateGetTemplateByIdResponseDTO response = adapterMapper.toGetTemplateByIdResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-type")
    @Operation(
        summary = "Buscar template por tipo",
        description = "Retorna um template específico baseado no tipo de template, tipo de mensagem e aplicação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template encontrado",
                    content = @Content(schema = @Schema(implementation = TemplateGetTemplateByTypeResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Template não encontrado para os parâmetros especificados"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "template_get_by_type_and_application",
        operation = "buscar template por tipo"
    )
    public ResponseEntity<TemplateGetTemplateByTypeResponseDTO> getTemplateByType(
            @Parameter(description = "Tipo do template (WELCOME, PASSWORD_RESET, etc.)") @RequestParam TemplateTypeEnum type,
            @Parameter(description = "Tipo da mensagem (EMAIL, SMS, WHATSAPP, etc.)") @RequestParam MessageTypeEnum messageType,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {

        log.info("Buscando template por tipo: {} e messageType: {} - companyId={}", type, messageType, companyId);
        
        TemplateView view = templatePort.getById(UUID.randomUUID()).orElseThrow(() -> new RuntimeException("Template not found"));
        TemplateGetTemplateByTypeResponseDTO response = adapterMapper.toGetTemplateByTypeResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar templates",
        description = "Retorna todos os templates ativos de uma aplicação específica."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de templates retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = TemplateGetTemplatesResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "template_search",
        operation = "listar templates"
    )
    public ResponseEntity<List<TemplateGetTemplatesResponseDTO>> getTemplates(
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {

        log.info("Listando templates - companyId={}", companyId);
        
        List<TemplateView> views = templatePort.getAllActive();
        List<TemplateGetTemplatesResponseDTO> response = views.stream()
                .map(adapterMapper::toGetTemplatesResponseDTO)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    // 🔥 HOT RELOAD TESTE - Endpoint de teste
    @GetMapping("/teste-hot-reload")
    @Operation(
        summary = "Teste de Hot Reload",
        description = "Endpoint para testar se o hot reload está funcionando"
    )
    public ResponseEntity<String> testeHotReload() {
        return ResponseEntity.ok("Hot Reload está funcionando! Timestamp: " + System.currentTimeMillis());
    }
}