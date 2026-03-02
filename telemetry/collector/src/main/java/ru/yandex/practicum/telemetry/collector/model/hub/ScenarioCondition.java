package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScenarioCondition(@NotBlank String sensorId, @NotNull ConditionType type,
                                @NotNull ConditionOperation operation, Object value) {}
