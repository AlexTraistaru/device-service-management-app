/** Clasa pentru o piesa din stoc.
 * clasa asta tine campurile din tabela Piese si este folosita la afisare si editare stoc.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

import java.math.BigDecimal;

public class piesa {

    // id-ul piesei (PiesaID)
    private Integer piesaId;

    // denumirea piesei
    private String denumire;

    // cod intern (poate fi folosit la cautare)
    private String cod;

    // pretul unei bucati
    private BigDecimal pret;

    // furnizorul piesei
    private String furnizor;

    // cate bucati sunt in stoc
    private Integer stoc;

    // returneaza PiesaID
    public Integer getPiesaId() { return piesaId; }

    // seteaza PiesaID
    public void setPiesaId(Integer piesaId) { this.piesaId = piesaId; }

    // returneaza denumirea
    public String getDenumire() { return denumire; }

    // seteaza denumirea
    public void setDenumire(String denumire) { this.denumire = denumire; }

    // returneaza codul
    public String getCod() { return cod; }

    // seteaza codul
    public void setCod(String cod) { this.cod = cod; }

    // returneaza pretul
    public BigDecimal getPret() { return pret; }

    // seteaza pretul
    public void setPret(BigDecimal pret) { this.pret = pret; }

    // returneaza furnizorul
    public String getFurnizor() { return furnizor; }

    // seteaza furnizorul
    public void setFurnizor(String furnizor) { this.furnizor = furnizor; }

    // returneaza stocul
    public Integer getStoc() { return stoc; }

    // seteaza stocul
    public void setStoc(Integer stoc) { this.stoc = stoc; }
}
