package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NotNull
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
