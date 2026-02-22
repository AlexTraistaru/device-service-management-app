/** Clasa pentru un obiect de afisare al unei comenzi in pagina angajatului.
 * clasa asta tine date adunate din mai multe tabele, ca sa afisam totul intr-un singur loc:
 * comanda + client + dispozitiv + departament + angajat.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.time.LocalDate;

// obiect folosit la afisare pentru angajat (date din join-uri)
public class comanda_angajat_view {

    // id-ul comenzii (ComandaID)
    private Integer comandaId;

    // date din comanda
    private LocalDate dataPrimire;
    private String defectDispozitiv;
    private String status;
    private String metodaDePlata;

    // date despre clientul care a facut comanda
    private Integer clientId;
    private String clientNume;
    private String clientPrenume;

    // date despre dispozitivul din comanda
    private Integer dispozitivId;
    private String tipDispozitiv;
    private String producator;
    private String model;
    private String serie;

    // date despre departamentul dispozitivului
    private Integer departamentId;
    private String departamentDenumire;

    // date despre angajatul asignat comenzii
    private Integer angajatId;
    private String angajatNume;
    private String angajatPrenume;

    // returneaza ComandaID
    public Integer getComandaId() { return comandaId; }

    // seteaza ComandaID
    public void setComandaId(Integer comandaId) { this.comandaId = comandaId; }

    // returneaza data primirii comenzii
    public LocalDate getDataPrimire() { return dataPrimire; }

    // seteaza data primirii comenzii
    public void setDataPrimire(LocalDate dataPrimire) { this.dataPrimire = dataPrimire; }

    // returneaza descrierea defectului scrisa in comanda
    public String getDefectDispozitiv() { return defectDispozitiv; }

    // seteaza descrierea defectului
    public void setDefectDispozitiv(String defectDispozitiv) { this.defectDispozitiv = defectDispozitiv; }

    // returneaza statusul comenzii
    public String getStatus() { return status; }

    // seteaza statusul comenzii
    public void setStatus(String status) { this.status = status; }

    // returneaza metoda de plata
    public String getMetodaDePlata() { return metodaDePlata; }

    // seteaza metoda de plata
    public void setMetodaDePlata(String metodaDePlata) { this.metodaDePlata = metodaDePlata; }

    // returneaza ClientID
    public Integer getClientId() { return clientId; }

    // seteaza ClientID
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    // returneaza numele clientului
    public String getClientNume() { return clientNume; }

    // seteaza numele clientului
    public void setClientNume(String clientNume) { this.clientNume = clientNume; }

    // returneaza prenumele clientului
    public String getClientPrenume() { return clientPrenume; }

    // seteaza prenumele clientului
    public void setClientPrenume(String clientPrenume) { this.clientPrenume = clientPrenume; }

    // returneaza DispozitivID
    public Integer getDispozitivId() { return dispozitivId; }

    // seteaza DispozitivID
    public void setDispozitivId(Integer dispozitivId) { this.dispozitivId = dispozitivId; }

    // returneaza tipul dispozitivului
    public String getTipDispozitiv() { return tipDispozitiv; }

    // seteaza tipul dispozitivului
    public void setTipDispozitiv(String tipDispozitiv) { this.tipDispozitiv = tipDispozitiv; }

    // returneaza producatorul dispozitivului
    public String getProducator() { return producator; }

    // seteaza producatorul
    public void setProducator(String producator) { this.producator = producator; }

    // returneaza modelul dispozitivului
    public String getModel() { return model; }

    // seteaza modelul
    public void setModel(String model) { this.model = model; }

    // returneaza seria dispozitivului
    public String getSerie() { return serie; }

    // seteaza seria dispozitivului
    public void setSerie(String serie) { this.serie = serie; }

    // returneaza DepartamentID
    public Integer getDepartamentId() { return departamentId; }

    // seteaza DepartamentID
    public void setDepartamentId(Integer departamentId) { this.departamentId = departamentId; }

    // returneaza denumirea departamentului
    public String getDepartamentDenumire() { return departamentDenumire; }

    // seteaza denumirea departamentului
    public void setDepartamentDenumire(String departamentDenumire) { this.departamentDenumire = departamentDenumire; }

    // returneaza AngajatID
    public Integer getAngajatId() { return angajatId; }

    // seteaza AngajatID
    public void setAngajatId(Integer angajatId) { this.angajatId = angajatId; }

    // returneaza numele angajatului asignat
    public String getAngajatNume() { return angajatNume; }

    // seteaza numele angajatului asignat
    public void setAngajatNume(String angajatNume) { this.angajatNume = angajatNume; }

    // returneaza prenumele angajatului asignat
    public String getAngajatPrenume() { return angajatPrenume; }

    // seteaza prenumele angajatului asignat
    public void setAngajatPrenume(String angajatPrenume) { this.angajatPrenume = angajatPrenume; }

    // intoarce true daca status este "Trimisa"
    public boolean esteTrimisa() { return "Trimisa".equalsIgnoreCase(status); }

    // intoarce true daca status este "In derulare"
    public boolean esteInDerulare() { return "In derulare".equalsIgnoreCase(status); }

    // intoarce true daca status este "Finalizata"
    public boolean esteFinalizata() { return "Finalizata".equalsIgnoreCase(status); }
}
