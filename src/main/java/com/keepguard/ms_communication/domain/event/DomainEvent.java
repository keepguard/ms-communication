package com.keepguard.ms_communication.domain.event;

/**
 * Interface marcadora para eventos de domínio.
 * 
 * <p>Segue os princípios do Domain-Driven Design (DDD), onde eventos de domínio
 * representam fatos que ocorreram no passado e são relevantes para o negócio.</p>
 * 
 * <p>Características dos eventos de domínio:</p>
 * <ul>
 *   <li>São imutáveis (Value Objects)</li>
 *   <li>Representam fatos do passado (nomes no passado)</li>
 *   <li>Contêm apenas dados relevantes ao evento</li>
 *   <li>Podem ser serializados para mensageria</li>
 * </ul>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
public interface DomainEvent {
    
    /**
     * Identificador de correlação para rastrear eventos relacionados.
     * 
     * @return ID de correlação único
     */
    String correlationId();
}
