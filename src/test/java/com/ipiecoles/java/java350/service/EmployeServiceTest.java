package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("T00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("M00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("M00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.85, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterNoLastMatricule() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals("M00001", employeArgumentCaptor.getValue().getMatricule());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterExistingEmploye(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());

        //When/Then
        EntityExistsException e = Assertions.assertThrows(EntityExistsException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("L'employé de matricule M00001 existe déjà en BDD", e.getMessage());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster99999(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When/Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("Limite des 100000 matricules atteinte !", e.getMessage());
    }


    //TESTER METHODE calculPerformanceCommercial
    //Tester si matricule null
    //Tester si cA traite  null
    //Tester si objectif cA null
    //Tester si cA traite est négatif
    //Tester si objectif cA négatif
    @Test
    public void testCalculPerformanceCommercialMatriculeIsNull(){
        //Given
        String matricule = null;
        Long caTraite = 15L;
        Long objectifCa = 15L;

        //When
        //Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));
        Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !", e.getMessage());
    }
    @Test
    public void testCalculPerformanceCommercialcATraiteIsNull(){
        //Given
        String matricule = "C00001";
        Long caTraite = null;
        Long objectifCa = 15L;

        //When
        //Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));
        Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !", e.getMessage());
    }
    @Test
    public void testCalculPerformanceCommercialObjectifCaIsNull(){
        //Given
        String matricule = "C00001";
        Long caTraite = 15L;
        Long objectifCa = null;

        //When
        //Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));
        Assertions.assertEquals("L'objectif de chiffre d'affaire ne peut être négatif ou null !", e.getMessage());
    }

    @Test
    public void testCalculPerformanceCommercialCaTraiteIsNegative(){
        //Given
        String matricule = "C00001";
        Long caTraite = -15L;
        Long objectifCa = 15L;

        //When
        //Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));
        Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !", e.getMessage());
    }

    @Test
    public void testCalculPerformanceCommercialObjectifCaIsNegative(){
        //Given
        String matricule = "C00001";
        Long caTraite = 15L;
        Long objectifCa = -15L;

        //When
        //Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));
        Assertions.assertEquals("L'objectif de chiffre d'affaire ne peut être négatif ou null !", e.getMessage());
    }

    //Test parametre pour performance attendu
    //Si cA > objCa * 0.8 && cA < objCa * 0.95
    //Si cA > objCa * 0.95 && cA < objCa * 1.05
    //Si cA < objCa * 1.2 && cA > objCa * 1.05
    //Si cA > objCa * 1.2
    @ParameterizedTest(name = "performance {0}, matricule {1}, caTraite {2}, objectifCa {3}, performanceAttendu {4}")
    @CsvSource({
            "1, 'C00001', 1000, 1100, 1",
            "1, 'C00001', 1000, 1050, 1",
            "1, 'C00001', 1000, 950, 3",
            "1, 'C00001', 1000, 0, 6",
    })
    public void testCalculPerformanceCommercialAttendu(Integer performance, String matricule, Long caTraite, Long objectifCa, Integer performanceAttendu) throws EmployeException {
        //Given
        Employe employe = new Employe("Doe", "Jonh", matricule, LocalDate.now(), Entreprise.SALAIRE_BASE, performance, 1.0);
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(employe);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(1.0);
        when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(performanceAttendu, employeArgumentCaptor.getValue().getPerformance());
    }
}