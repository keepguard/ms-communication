package com.keepguard.ms_communication.application.port.out.cache;

import com.keepguard.ms_communication.application.dto.template.TemplateCacheView;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;

public interface TemplateCachePort {

    // By Template Type, Message Type and Application
    void cacheTemplate(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application, TemplateCacheView template);
    TemplateCacheView getTemplateFromCache(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application);
    void removeTemplateFromCache(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application);

    // Clear All
    void clearAllTemplateCache();

}
