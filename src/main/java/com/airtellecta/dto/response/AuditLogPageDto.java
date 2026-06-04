package com.airtellecta.dto.response;

import java.util.List;

public class AuditLogPageDto {
    public List<AuditLogItemDto> items;
    public long  totalItems;
    public int   totalPages;
    public int   page;
    public int   size;
}
