/** Clasa pentru afisarea comenzilor unui client in pagina "comenzile mele".
 * clasa asta tine date adunate din mai multe tabele, ca sa afisam comanda impreuna cu dispozitivul si departamentul.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.time.LocalDate;

// obiect folosit la afisare in pagina "comenzile mele"
public class comanda_view {

    // id-ul comenzii (ComandaID)
    private Integer comandaId;

    // date din comanda
    private LocalDate dataPrimire;
    private String defectDispozitiv;
    private String status;
    private String metodaDePlata;

    // date din dispozitivul din comanda
    private Integer dispozitivId;
    private String tipDispozitiv;
    private String producator;
    private String model;
    private String serie;

    // date din departamentul dispozitivului
    private Integer departamentId;
    private String departamentDenumire;

    // returneaza ComandaID
    public Integer getComandaId() { return comandaId; }

    // seteaza ComandaID
    public void setComandaId(Integer comandaId) { this.comandaId = comandaId; }

    // returneaza data primirii comenzii
    public LocalDate getDataPrimire() { return dataPrimire; }

    // seteaza data primirii comenzii
    public void setDataPrimire(LocalDate dataPrimire) { this.dataPrimire = dataPrimire; }

    // returneaza descrierea defectului
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

    // returneaza DispozitivID
    public Integer getDispozitivId() { return dispozitivId; }

    // seteaza DispozitivID
    public void setDispozitivId(Integer dispozitivId) { this.dispozitivId = dispozitivId; }

    // returneaza tipul dispozitivului
    public String getTipDispozitiv() { return tipDispozitiv; }

    // seteaza tipul dispozitivului
    public void setTipDispozitiv(String tipDispozitiv) { this.tipDispozitiv = tipDispozitiv; }

    // returneaza producatorul
    public String getProducator() { return producator; }

    // seteaza producatorul
    public void setProducator(String producator) { this.producator = producator; }

    // returneaza modelul
    public String getModel() { return model; }

    // seteaza modelul
    public void setModel(String model) { this.model = model; }

    // returneaza seria
    public String getSerie() { return serie; }

    // seteaza seria
    public void setSerie(String serie) { this.serie = serie; }

    // returneaza DepartamentID
    public Integer getDepartamentId() { return departamentId; }

    // seteaza DepartamentID
    public void setDepartamentId(Integer departamentId) { this.departamentId = departamentId; }

    // returneaza denumirea departamentului
    public String getDepartamentDenumire() { return departamentDenumire; }

    // seteaza denumirea departamentului
    public void setDepartamentDenumire(String departamentDenumire) { this.departamentDenumire = departamentDenumire; }

    // intoarce true daca status este "trimisa"
    // in ui asta inseamna ca clientul inca poate edita sau sterge comanda
    public boolean esteEditabila() {
        return "Trimisa".equalsIgnoreCase(status);
    }
}
