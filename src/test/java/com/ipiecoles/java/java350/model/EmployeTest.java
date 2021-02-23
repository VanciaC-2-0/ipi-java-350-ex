package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {
    @Test
    public void testGetNbAnneeAncienneteDateEmbaucheNull() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);
        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();
        //Then
        Assertions.assertThat(nbAnneeAnciennete).isNull();
    }

    @Test
    public void testGetNbAnneeAncienneteDateEmbaucheInfNow() {
        //Given
        Employe employe = new Employe("Doe", "John", "T12345",
                LocalDate.now().minusYears(6), 1500d, 1, 1.0);

        //When
        Integer anneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete).isEqualTo(6);
    }

    @Test
    public void testGetNbAnneeAncienneteDateEmbaucheNow() {
        //Given
        Employe employe = new Employe("Doe", "John", "T12345",
                LocalDate.now(), 1500d, 1, 1.0);
        //When
        Integer anneeAnciennete = employe.getNombreAnneeAnciennete();
        //Then
        Assertions.assertThat(anneeAnciennete).isEqualTo(0);
    }

    @ParameterizedTest(name = "Perf {0}, matricule {1}, txActivite {2}, anciennete {3} => prime {4}")
    @CsvSource({
            "1, 'T12345', 1.0, 0, 1000.0",
            "1, 'T12345', 0.5, 0, 500.0",
            "2, 'T12345', 1.0, 0, 2300.0",
            "1, 'T12345', 1.0, 2, 1200.0",
            "2, 'T123456', 1.0, 1, 2400.0",
            "1, 'M12345', 1.0, 0, 1700.0",
            "1, 'M12345', 1.0, 3, 2000.0",
            "1, 'M12345', 0.0, 0, 0.0",
    })
    public void testGetPrimeAnnuelle(Integer performance, String matricule, Double tauxActivite, Long nbAnneesAnciennete,
                                     Double primeAttendue) {
        //Given
        Employe employe = new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(nbAnneesAnciennete), 1500d,
                performance, tauxActivite);
        //When
        Double prime = employe.getPrimeAnnuelle();
        //Then
        Assertions.assertThat(prime).isEqualTo(primeAttendue);
    }

    @Test
    public void testGetPrimeAnnuelleMatriculeNull() {
        //Given
        Employe employe = new Employe("Doe", "Jonh", null, LocalDate.now(), 1500d, 1, 1.0);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(prime).isEqualTo(1000.0);
    }


    //TEST DE LA METHODE augmenterSalaire

    //Test augmentation basique en changeant le salaire et le pourcentage
    //Test si salaire est null
    //Test si pourcentage est 0
    //Test si pourcentage est négative

    @ParameterizedTest(name = "salaireBase {0}, pourcentageAugmentation {1}, salaireApresAugmentation {2}")
    @CsvSource({
            "1000.0, 10.0, 1100.0",
            "1000.0, 50.0, 1500.0",
            "2500.0, 10.0, 2750.0",
            "2000.0, 15.5, 2310.0",
            "1234, 28.5, 1585.69"
    })
    private void testAugmenterSalaireBase(double salaireBase, double pourcentage, double salaireApresAugmentation) {
        //Given
        Employe employe = new Employe();
        employe.setSalaire(salaireBase);
        //When
        employe.augmenterSalaire(pourcentage);
        //Then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaireApresAugmentation);
    }

    @Test
    public void testAugmenterSalaireIfSalaireIsNull() {
        //Given
        Employe employe = new Employe();
        employe.setSalaire(null);
        //When
        employe.augmenterSalaire(10.0);
        //Then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1673.34);
    }

    @Test
    public void testAugmenterSalaireIfPourcentageIsZero() {
        //Given
        Employe employe = new Employe();
        employe.setSalaire(1000.0);
        //When
        employe.augmenterSalaire(0.0);
        //Then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1000.0);
    }

    @Test
    public void testAugmenterSalaireIfPourcentageIsNegative() {
        //Given
        Employe employe = new Employe();
        employe.setSalaire(1000.0);
        //When
        employe.augmenterSalaire(-10.0);
        //Then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1000.0);
    }


    //TEST Methode getNbRtt
    //Tester de façon parametré la méthode
    //Nb jours max forfait: 218j
    // 2019: mardi 365j/an; 104j sam/dim; 25j CB; 10j fériés pas w-e.
    // 2021: vendredi 365j/an; 105j sam/dim; 25j CB; 7j fériés pas w-e.
    // 2022: samedi 365j/an; 105j sam/dim; 25j CB; 7j férié pas w-e
    // 2032: jeudi 366j/an; 105j sam/dim; 25j CB; 7j férié pas w-e.
    // 2024: lundi 366j/an; 104j sam/dim; 25j CB; 7j férié pas w-e.
    // 2044: vendredi 366j/an; 106j sam/dim; 25j CB; 10j férié pas w-e.
    // 2026: jeudi 365j/an; 104j sam/dim; 25j CB; 9j férier pas w-e
    @ParameterizedTest(name = "dateRef {0}, nbRttAttendu {1}")
    @CsvSource({
            "2019-01-01, 8",
            "2021-01-01, 10",
            "2022-01-01, 10",
            "2032-01-01, 11",
            "2024-01-01, 9",
            "2044-01-01, 9",
            "2026-01-01, 9"
    })
    private void testGetNbRtt(LocalDate dateRef, Integer nbRttAttendu){
        //Given
        Employe employe = new Employe();
        //When
        int nbRtt = employe.getNbRtt(dateRef);
        //Then
        Assertions.assertThat(nbRtt).isEqualTo(nbRttAttendu);
    }
}
