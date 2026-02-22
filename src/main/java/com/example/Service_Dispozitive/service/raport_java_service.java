/** Clasa pentru procesarea in java a unui raport.
 * aici se ia o lista din sql (facturi cu nume client) si se proceseaza in java:
 * - se grupeaza pe client
 * - se calculeaza pretul maxim pe client
 * - se numara cate facturi are clientul
 * - se sorteaza descrescator si se ia top n
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.service;

import com.example.Service_Dispozitive.acces_date.factura_acces;
import com.example.Service_Dispozitive.entitati.factura_client_view;
import com.example.Service_Dispozitive.entitati.top_client_max_view;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class raport_java_service {

    // acces la facturi (de aici luam lista pentru interval)
    private final factura_acces facturi;

    public raport_java_service(factura_acces facturi) {
        this.facturi = facturi;
    }

    // intoarce top clienti dupa cea mai mare factura din interval
    // from = data inceput (inclusiv)
    // to = data final (inclusiv)
    // top = cate pozitii vrem (daca top <= 0, folosim 5)
    public List<top_client_max_view> topClientiDupaMaxFactura(LocalDate from, LocalDate to, int top) {

        // validari pentru interval
        if (from == null || to == null) throw new IllegalStateException("Interval date invalid.");
        if (to.isBefore(from)) throw new IllegalStateException("Data finala nu poate fi inainte de data initiala.");
        if (top <= 0) top = 5;

        // luam lista din sql: fiecare element are (clientNumeComplet, pretFactura, dataEmitere)
        List<factura_client_view> lista = facturi.listFacturiCuClientInterval(from, to);

        // maxPret: pentru fiecare client tinem cel mai mare pretFactura gasit pana acum
        Map<String, BigDecimal> maxPret = new HashMap<>();

        // cnt: pentru fiecare client tinem cate facturi are in lista
        Map<String, Integer> cnt = new HashMap<>();

        // trecem prin toate facturile din interval si actualizam max si count
        for (factura_client_view f : lista) {

            // daca numele clientului nu exista, punem un text default
            String client = (f.getClientNumeComplet() == null || f.getClientNumeComplet().isBlank())
                    ? "Client fara nume" : f.getClientNumeComplet();

            // daca pretul e null, il tratam ca 0
            BigDecimal pret = (f.getPretFactura() == null) ? BigDecimal.ZERO : f.getPretFactura();

            // incrementam numarul de facturi pentru client
            cnt.put(client, cnt.getOrDefault(client, 0) + 1);

            // actualizam pretul maxim pentru client daca pretul curent este mai mare
            if (!maxPret.containsKey(client) || pret.compareTo(maxPret.get(client)) > 0) {
                maxPret.put(client, pret);
            }
        }

        // construim lista finala din map-uri
        List<top_client_max_view> rezultat = new ArrayList<>();

        for (String client : maxPret.keySet()) {
            // pentru fiecare client:
            // - nume complet
            // - maxPretFactura (din maxPret)
            // - numar facturi (din cnt)
            rezultat.add(new top_client_max_view(client, maxPret.get(client), cnt.getOrDefault(client, 0)));
        }

        // sortam descrescator dupa maxPretFactura
        rezultat.sort((a, b) -> b.getMaxPretFactura().compareTo(a.getMaxPretFactura()));

        // luam doar top n daca lista e mai mare
        if (rezultat.size() > top) {
            return rezultat.subList(0, top);
        }

        return rezultat;
    }
}
