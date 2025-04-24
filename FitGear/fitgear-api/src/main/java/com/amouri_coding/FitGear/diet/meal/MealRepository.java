package com.amouri_coding.FitGear.diet.meal;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;

public interface MealRepository extends JpaAttributeConverter<Meal, Long> {
}
