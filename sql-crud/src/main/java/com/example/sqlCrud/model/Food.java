package com.example.sqlCrud.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @Column(name = "food_id")
    String foodId;

    @Column(name = "food_name")
    String foodName;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Column(name = "ingredient")
    @CollectionTable(name = "ingredients", joinColumns = @JoinColumn(name = "food_id"))
    List<String> foodIngredients;

    @Column(name = "food_type")
    @Enumerated(value = EnumType.STRING) // this annotation will allow us to save ENUM as String in column
    FoodType foodType;
}
