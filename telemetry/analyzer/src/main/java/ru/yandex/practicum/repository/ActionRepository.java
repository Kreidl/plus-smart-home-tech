package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Action;

import java.util.Collection;

public interface ActionRepository extends JpaRepository<Action, Long> {
    void deleteByScenarioId(Long scenarioId);

    Collection<Action> findAllByScenarioId(Long scenarioId);
}