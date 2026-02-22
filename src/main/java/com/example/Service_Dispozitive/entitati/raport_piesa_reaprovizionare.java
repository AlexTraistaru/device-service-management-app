/** Clasa pentru un rand din raportul "piese sub prag + consum in ultimele n zile".
 * clasa asta tine denumirea piesei, stocul curent si cate bucati s-au folosit in ultimele zile.
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

// obiect folosit la afisarea unui rand din raport
public class raport_piesa_reaprovizionare {

    // denumirea piesei
    private String denumire;

    // stocul curent
    private Integer stoc;

    // cate bucati au fost folosite in ultimele n zile
    private Integer folositeUltimeleZile;

    // returneaza denumirea
    public String getDenumire() { return denumire; }

    // seteaza denumirea
    public void setDenumire(String denumire) { this.denumire = denumire; }

    // returneaza stocul
    public Integer getStoc() { return stoc; }

    // seteaza stocul
    public void setStoc(Integer stoc) { this.stoc = stoc; }

    // returneaza cate s-au folosit
    public Integer getFolositeUltimeleZile() { return folositeUltimeleZile; }

    // seteaza cate s-au folosit
    public void setFolositeUltimeleZile(Integer folositeUltimeleZile) { this.folositeUltimeleZile = folositeUltimeleZile; }
}
