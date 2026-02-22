/** Clasa pentru un departament (categorie).
 * clasa asta tine campurile din tabela Departament si este folosita in special la formulare.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

// obiect simplu pentru un rand din tabela Departament
public class departament {

    // id-ul departamentului (DepartamentID)
    private Integer departamentId;

    // numele departamentului (ex: electrocasnice)
    private String denumire;

    // returneaza DepartamentID
    public Integer getDepartamentId() { return departamentId; }

    // seteaza DepartamentID
    public void setDepartamentId(Integer departamentId) { this.departamentId = departamentId; }

    // returneaza denumirea departamentului
    public String getDenumire() { return denumire; }

    // seteaza denumirea departamentului
    public void setDenumire(String denumire) { this.denumire = denumire; }
}
