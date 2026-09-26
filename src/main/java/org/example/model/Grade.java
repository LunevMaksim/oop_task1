package org.example.model;

import org.example.exception.InvalidScoreException;

// Допустимые оценки
public enum Grade {

    EXCELLENT,
    GOOD,
    SATISFACTORY,
    UNSATISFACTORY;

    public static Grade fromScore(int score){
        if (score < 0 || score > 100) throw new InvalidScoreException("Ошибка! Баллы не соответствуют шкале БРС");
        if (score >= 0 && score < 50) return UNSATISFACTORY;
        else if (score >= 50 && score < 70) return SATISFACTORY;
        else if (score >= 70 && score < 90) return GOOD;
        return EXCELLENT;
    }

}
