package org.example.conf;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Singleton-класс, предоставляющий доступ к единственному экземпляру себя.
 */
@Component
public class SingletonBean {

    /**
     * -- GETTER --
     *  Возвращает единственный экземпляр класса.
     */
    @Getter
    private static SingletonBean instance;

    private SingletonBean() {
    }
    /**
     * Инициализирует единственный экземпляр класса.
     */
    @PostConstruct
    public void init() {
        instance = this;
    }
}
