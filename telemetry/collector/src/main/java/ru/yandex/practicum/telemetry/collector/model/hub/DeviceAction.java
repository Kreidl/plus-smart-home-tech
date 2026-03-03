package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceAction(@NotBlank String sensorId, @NotNull ActionType type, Integer value) {}
