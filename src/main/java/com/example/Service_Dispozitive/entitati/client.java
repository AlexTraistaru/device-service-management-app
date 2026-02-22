/** Clasa pentru modelul de date al unui client.
 * clasa asta tine campurile din tabela Clienti
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.entitati;

// obiect simplu pentru un rand din tabela Clienti
public class client {

    // cheie primara din tabela Clienti
    private Integer clientId;

    // legatura catre tabela Utilizatori (contul de login)
    private Integer userId;

    // date de identificare minima pentru client
    private String nume;
    private String prenume;
    private String telefon;

    // date optionale
    private String email;
    private String cnp;

    // adresa (optionala)
    private String strada;
    private String numar;
    private String oras;
    private String judet;

    // returneaza id-ul clientului (ClientID)
    public Integer getClientId() { return clientId; }

    // seteaza id-ul clientului (ClientID)
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    // returneaza userId (UserID din Utilizatori)
    public Integer getUserId() { return userId; }

    // seteaza userId (UserID din Utilizatori)
    public void setUserId(Integer userId) { this.userId = userId; }

    // returneaza numele clientului
    public String getNume() { return nume; }

    // seteaza numele clientului
    public void setNume(String nume) { this.nume = nume; }

    // returneaza prenumele clientului
    public String getPrenume() { return prenume; }

    // seteaza prenumele clientului
    public void setPrenume(String prenume) { this.prenume = prenume; }

    // returneaza telefonul clientului
    public String getTelefon() { return telefon; }

    // seteaza telefonul clientului
    public void setTelefon(String telefon) { this.telefon = telefon; }

    // returneaza email-ul clientului
    public String getEmail() { return email; }

    // seteaza email-ul clientului
    public void setEmail(String email) { this.email = email; }

    // returneaza cnp-ul clientului
    public String getCnp() { return cnp; }

    // seteaza cnp-ul clientului
    public void setCnp(String cnp) { this.cnp = cnp; }

    // returneaza strada
    public String getStrada() { return strada; }

    // seteaza strada
    public void setStrada(String strada) { this.strada = strada; }

    // returneaza numarul (de la adresa)
    public String getNumar() { return numar; }

    // seteaza numarul (de la adresa)
    public void setNumar(String numar) { this.numar = numar; }

    // returneaza orasul
    public String getOras() { return oras; }

    // seteaza orasul
    public void setOras(String oras) { this.oras = oras; }

    // returneaza judetul
    public String getJudet() { return judet; }

    // seteaza judetul
    public void setJudet(String judet) { this.judet = judet; }
}
