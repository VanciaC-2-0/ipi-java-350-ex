package com.ipiecoles.java.java350.service;


import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.*;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    public void integrationEmbaucheEmploye() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Employe employe = employeRepository.findByMatricule("T12346");
        Assertions.assertNotNull(employe);
        Assertions.assertEquals(nom, employe.getNom());
        Assertions.assertEquals(prenom, employe.getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T12346", employe.getMatricule());
        Assertions.assertEquals(1.0, employe.getTempsPartiel().doubleValue());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employe.getSalaire().doubleValue());
    }

    @Test
    public void integationCalculPerformanceCommercial() throws EmployeException{
        //Given
        String nom = "Kara";
        String prenom = "Age";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        String matricule = "C00001";
        Long caTraite = 1500L;
        Long objectifCa = 1000L;

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //Then
        Employe employe = employeRepository.findByMatricule(matricule);
        Assertions.assertEquals(6, employe.getPerformance());
    }

    @Test
    public void integrationCalculPerformanceCommercialAvgPerf(){
        //Given
        Employe employe1 = new Employe("Katsu", "Don", "C00001", LocalDate.now(), Entreprise.SALAIRE_BASE, 2, 1.0);
        Employe employe2 = new Employe("Okono", "Miyaki", "C00002", LocalDate.now(),Entreprise.SALAIRE_BASE, 4, 1.0);
        employeRepository.save(employe1);
        employeRepository.save(employe2);
        //When
        Double avgPerformance = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");
        //Then
        Assertions.assertEquals(3.0, avgPerformance);
    }
}