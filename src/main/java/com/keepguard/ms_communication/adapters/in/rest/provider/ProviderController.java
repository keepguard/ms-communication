package com.keepguard.ms_communication.adapters.in.rest.provider;

import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderUpdateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.response.*;
import com.keepguard.ms_communication.adapters.in.rest.provider.mapper.ProviderAdapterMapper;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.application.mapper.ProviderApplicationMapper;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.ms_communication.application.port.in.service.ProviderPort;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Provedores", description = "APIs para gerenciamento de provedores de comunicação (N8N, SendGrid, etc.)")
public class ProviderController {

    private final ProviderPort providerPort;
    private final ProviderAdapterMapper adapterMapper;
    private final ProviderApplicationMapper applicationMapper;

    @PostMapping
    @Operation(
        summary = "Criar provedor",
        description = "Cria um novo provedor de comunicação com as configurações especificadas. " +
                    "O provedor será usado para enviar mensagens do tipo configurado.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do provedor a ser criado",
            required = true,
            content = @Content(schema = @Schema(implementation = ProviderCreateRequestDTO.class))
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Provedor criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProviderCreateResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Provedor já existe"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_create",
        operation = "criar provedor"
    )
    public ResponseEntity<ProviderCreateResponseDTO> createProvider(
            @Valid @RequestBody ProviderCreateRequestDTO dto,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Criando provider - companyId={}", companyId);
        
        com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO requestCommand = adapterMapper.toCreateCommand(dto, companyId);
        ProviderCreateCommandDTO command = applicationMapper.toCreateCommand(requestCommand);
        ProviderView view = providerPort.create(command);
        ProviderCreateResponseDTO response = adapterMapper.toCreateResponseDTO(view);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar provedor",
        description = "Atualiza as configurações de um provedor existente. " +
                    "Apenas provedores inativos podem ser atualizados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provedor atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProviderUpdateResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Provedor não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_update",
        operation = "atualizar provedor"
    )
    public ResponseEntity<ProviderUpdateResponseDTO> updateProvider(
            @Parameter(description = "ID único do provedor") @PathVariable UUID id,
            @Valid @RequestBody ProviderUpdateRequestDTO dto,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Atualizando provider: {} - companyId={}", id, companyId);
        
        com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO requestCommand = adapterMapper.toUpdateCommand(id, dto, companyId);
        ProviderUpdateCommandDTO command = applicationMapper.toUpdateCommand(requestCommand);
        ProviderView view = providerPort.update(command);
        ProviderUpdateResponseDTO response = adapterMapper.toUpdateResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar provedor por ID",
        description = "Retorna os detalhes de um provedor específico pelo seu ID único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provedor encontrado",
                    content = @Content(schema = @Schema(implementation = ProviderGetProviderByIdResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Provedor não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_get_by_id",
        operation = "buscar provedor por ID"
    )
    public ResponseEntity<ProviderGetProviderByIdResponseDTO> getProviderById(
            @Parameter(description = "ID único do provedor") @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Buscando provider por ID: {} - companyId={}", id, companyId);
        
        ProviderView view = providerPort.getById(id).orElseThrow(() -> new RuntimeException("Provider not found"));
        ProviderGetProviderByIdResponseDTO response = adapterMapper.toGetProviderByIdResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar provedores",
        description = "Retorna todos os provedores cadastrados no sistema, " +
                    "incluindo provedores ativos e inativos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de provedores retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProviderGetAllProvidersResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_search",
        operation = "listar provedores"
    )
    public ResponseEntity<List<ProviderGetAllProvidersResponseDTO>> getAllProviders(
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Listando todos providers - companyId={}", companyId);
        
        List<ProviderView> views = providerPort.getAllActive();
        List<ProviderGetAllProvidersResponseDTO> response = views.stream()
                .map(adapterMapper::toGetAllProvidersResponseDTO)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(
        summary = "Listar provedores ativos",
        description = "Retorna apenas os provedores que estão ativos e disponíveis para envio de mensagens."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de provedores ativos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProviderGetActiveProvidersResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_search",
        operation = "listar provedores ativos"
    )
    public ResponseEntity<List<ProviderGetActiveProvidersResponseDTO>> getActiveProviders(
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Listando providers ativos - companyId={}", companyId);
        
        List<ProviderView> views = providerPort.getAllActive();
        List<ProviderGetActiveProvidersResponseDTO> response = views.stream()
                .map(adapterMapper::toGetActiveProvidersResponseDTO)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{communicationType}")
    @Operation(
        summary = "Listar provedores por tipo",
        description = "Retorna provedores filtrados por tipo de comunicação (EMAIL, SMS, WHATSAPP, etc.)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de provedores do tipo especificado",
                    content = @Content(schema = @Schema(implementation = ProviderGetProvidersByCommunicationTypeResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Tipo de comunicação inválido"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_search",
        operation = "listar provedores por tipo"
    )
    public ResponseEntity<List<ProviderGetProvidersByCommunicationTypeResponseDTO>> getProvidersByCommunicationType(
            @Parameter(description = "Tipo de comunicação (EMAIL, SMS, WHATSAPP, PUSH_NOTIFICATION, TELEGRAM)")
            @PathVariable CommunicationTypeEnum communicationType,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Listando providers por tipo de comunicação: {} - companyId={}", communicationType, companyId);
        
        List<ProviderView> views = providerPort.getByCommunicationType(communicationType);
        List<ProviderGetProvidersByCommunicationTypeResponseDTO> response = views.stream()
                .map(adapterMapper::toGetProvidersByCommunicationTypeResponseDTO)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/default/{communicationType}")
    @Operation(
        summary = "Buscar provedor padrão",
        description = "Retorna o provedor configurado como padrão para um tipo de comunicação específico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provedor padrão encontrado",
                    content = @Content(schema = @Schema(implementation = ProviderGetDefaultProviderResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Provedor padrão não encontrado para o tipo especificado"),
        @ApiResponse(responseCode = "400", description = "Tipo de comunicação inválido"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_get_by_id",
        operation = "buscar provedor padrão"
    )
    public ResponseEntity<ProviderGetDefaultProviderResponseDTO> getDefaultProvider(
            @Parameter(description = "Tipo de comunicação para buscar o provedor padrão")
            @PathVariable CommunicationTypeEnum communicationType,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Buscando provider padrão por tipo: {} - companyId={}", communicationType, companyId);
        
        ProviderView view = providerPort.getDefaultByCommunicationType(communicationType)
                .orElseThrow(() -> new com.keepguard.ms_communication.application.service.exception.NotFoundException(
                        "Provedor padrão não encontrado para o tipo: " + communicationType,
                        "DEFAULT_PROVIDER_NOT_FOUND",
                        Map.of("communicationType", communicationType.name())
                ));
        ProviderGetDefaultProviderResponseDTO response = adapterMapper.toGetDefaultProviderResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar provedor",
        description = "Remove um provedor do sistema. Apenas provedores inativos podem ser deletados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Provedor deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Provedor não encontrado"),
        @ApiResponse(responseCode = "400", description = "Provedor ativo não pode ser deletado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_delete",
        operation = "deletar provedor"
    )
    public ResponseEntity<Void> deleteProvider(
            @Parameter(description = "ID único do provedor") @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Deletando provider: {} - companyId={}", id, companyId);
        
        providerPort.delete(id);
        
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(
        summary = "Ativar provedor",
        description = "Ativa um provedor, tornando-o disponível para envio de mensagens."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provedor ativado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProviderActivateProviderResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Provedor não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_activate",
        operation = "ativar provedor"
    )
    public ResponseEntity<ProviderActivateProviderResponseDTO> activateProvider(
            @Parameter(description = "ID único do provedor") @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Ativando provider: {} - companyId={}", id, companyId);
        
        ProviderView view = providerPort.activate(id);
        ProviderActivateProviderResponseDTO response = adapterMapper.toActivateProviderResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
        summary = "Desativar provedor",
        description = "Desativa um provedor, impedindo-o de enviar mensagens."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provedor desativado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProviderDeactivateProviderResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Provedor não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_deactivate",
        operation = "desativar provedor"
    )
    public ResponseEntity<ProviderDeactivateProviderResponseDTO> deactivateProvider(
            @Parameter(description = "ID único do provedor") @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Desativando provider: {} - companyId={}", id, companyId);
        
        ProviderView view = providerPort.deactivate(id);
        ProviderDeactivateProviderResponseDTO response = adapterMapper.toDeactivateProviderResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/default")
    @Operation(
        summary = "Definir como padrão",
        description = "Define um provedor como padrão para seu tipo de comunicação. " +
                    "Apenas um provedor por tipo pode ser padrão."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provedor definido como padrão com sucesso",
                    content = @Content(schema = @Schema(implementation = ProviderSetAsDefaultResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Provedor não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_set_default",
        operation = "definir como padrão"
    )
    public ResponseEntity<ProviderSetAsDefaultResponseDTO> setAsDefault(
            @Parameter(description = "ID único do provedor") @PathVariable UUID id,
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Definindo provider como padrão: {} - companyId={}", id, companyId);
        
        ProviderView view = providerPort.setAsDefault(id);
        ProviderSetAsDefaultResponseDTO response = adapterMapper.toSetAsDefaultResponseDTO(view);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    @Operation(
        summary = "Listar tipos de provedores",
        description = "Retorna todos os tipos de provedores disponíveis no sistema (N8N, SENDGRID, etc.)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tipos de provedores retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "provider_types",
        operation = "listar tipos de provedores"
    )
    public ResponseEntity<ProviderTypeEnum[]> getProviderTypes(
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Listando tipos de provider - companyId={}", companyId);
        
        return ResponseEntity.ok(ProviderTypeEnum.values());
    }

    @GetMapping("/communication-types")
    @Operation(
        summary = "Listar tipos de comunicação",
        description = "Retorna todos os tipos de comunicação suportados pelo sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tipos de comunicação retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @MetricsEndpoint(
        endpoint = "communication_types",
        operation = "listar tipos de comunicação"
    )
    public ResponseEntity<CommunicationTypeEnum[]> getCommunicationTypes(
            @Parameter(description = "UUID da empresa", required = true)
            @RequestHeader("X-Company-Id") UUID companyId) {
        
        log.info("Listando tipos de comunicação - companyId={}", companyId);
        
        return ResponseEntity.ok(CommunicationTypeEnum.values());
    }

}