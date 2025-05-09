package com.card_management.technical.exception.record;

import java.util.List;

public record ErrorResponse(List<FieldErrorDto> errors) {}
