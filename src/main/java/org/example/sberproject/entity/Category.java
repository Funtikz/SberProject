package org.example.sberproject.entity;


public enum Category {
    IT("IT услуги"),
    DESIGN("Дизайн и графика"),
    CONSULTING("Консалтинг"),
    MARKETING("Маркетинг и реклама"),
    TRANSLATION("Переводы"),
    EDUCATION("Обучение и курсы"),
    HEALTH("Здоровье и фитнес"),
    HOME_REPAIR("Ремонт и строительство"),
    LEGAL("Юридические услуги"),
    FINANCE("Финансовые услуги"),
    OTHER("Другие услуги");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}