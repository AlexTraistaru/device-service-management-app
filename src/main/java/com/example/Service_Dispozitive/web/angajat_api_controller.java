/** Clasa pentru endpoint-uri rest pentru postman
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.web;

import com.example.Service_Dispozitive.acces_date.comanda_angajat_acces;
import com.example.Service_Dispozitive.entitati.comanda_angajat_view;
import com.example.Service_Dispozitive.service.angajat_comanda_service;
import com.example.Service_Dispozitive.service.raport_java_service;
import com.example.Service_Dispozitive.entitati.top_client_max_view;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API pentru demo Postman
 */
@RestController
@RequestMapping("/api")
// controller rest pentru postman (json)
public class angajat_api_controller {

    // acces la interogarile pentru comenzi (join-uri pentru afisare)
    private final comanda_angajat_acces comenzi;

    // logica de business pentru actiuni pe comenzi (ex: trimisa - in derulare)
    private final angajat_comanda_service comandaService;

    // raport procesat in java (group by + max + sort)
    private final raport_java_service rapoarte;

    public angajat_api_controller(comanda_angajat_acces comenzi,
                                  angajat_comanda_service comandaService,
                                  raport_java_service rapoarte) {
        this.comenzi = comenzi;
        this.comandaService = comandaService;
        this.rapoarte = rapoarte;
    }

    /**
     * 1) GET /api/angajat/comenzi?status=Trimisa
     * - lista comenzi (toate), optional filtrate dupa status.
     */
    @GetMapping("/angajat/comenzi")
    public List<ComandaApiDto> getComenzi(@RequestParam(required = false) String status) {

        // daca nu vine status, luam toate comenzile
        List<comanda_angajat_view> lista;
        if (status == null || status.trim().isEmpty()) {
            lista = comenzi.listToate();
        } else {
            // daca vine status, filtram direct in sql
            lista = comenzi.listToateFiltratStatus(status.trim());
        }

        // transformam obiectele mari (cu id-uri) intr-un dto simplu pentru json
        return lista.stream().map(c -> new ComandaApiDto(
                // nume complet client, fara null-uri
                (c.getClientNume() == null ? "" : c.getClientNume()) + " " + (c.getClientPrenume() == null ? "" : c.getClientPrenume()),
                c.getDepartamentDenumire(),
                c.getTipDispozitiv(),
                c.getProducator(),
                c.getModel(),
                c.getSerie(),
                c.getDataPrimire() == null ? null : c.getDataPrimire().toString(),
                c.getStatus()
        )).collect(Collectors.toList());
    }

    /**
     * 2) POST /api/angajat/comenzi/{id}/status
     * in postman trimitem json: { "status": "In derulare" }
     * - permitem doar "in derulare" si folosim regula existenta (trimisa -> in derulare).
     */
    @PostMapping("/angajat/comenzi/{id}/status")
    public ApiRaspuns postStatus(@PathVariable("id") int comandaId,
                                 @RequestBody StatusRequest req,
                                 Authentication auth) {

        // citim status-ul din json si il curatam
        String statusNou = (req == null || req.status == null) ? "" : req.status.trim();

        // pentru demo acceptam doar "in derulare"
        if (!"In derulare".equalsIgnoreCase(statusNou)) {
            return new ApiRaspuns(false, "Pentru demo permitem doar status = 'In derulare'.");
        }

        // folosim regula din service: pornesteLucrul (trimisa -> in derulare)
        comandaService.pornesteLucrul(auth.getName(), comandaId);

        return new ApiRaspuns(true, "Status setat pe In derulare.");
    }

    /**
     * 3) GET /api/rapoarte/top-clienti-max?from=2026-01-01&to=2026-01-12&top=5
     * aici e procesarea in java (group by + max + sort).
     * intervalul este parametru variabil.
     */
    @GetMapping("/rapoarte/top-clienti-max")
    public List<top_client_max_view> topClientiMax(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "5") int top
    ) {
        return rapoarte.topClientiDupaMaxFactura(from, to, top);
    }

    // dto-uri simple pentru api

    // body pentru post status: { "status": "In derulare" }
    public static class StatusRequest {
        public String status;
    }

    // raspuns simplu: ok + mesaj
    public static class ApiRaspuns {
        public boolean ok;
        public String mesaj;

        public ApiRaspuns(boolean ok, String mesaj) {
            this.ok = ok;
            this.mesaj = mesaj;
        }
    }

    // dto pentru lista de comenzi (fara id-uri)
    public static class ComandaApiDto {
        public String clientNumeComplet;
        public String departament;
        public String tip;
        public String producator;
        public String model;
        public String serie;
        public String dataPrimire;
        public String status;

        public ComandaApiDto(String clientNumeComplet, String departament, String tip,
                             String producator, String model, String serie,
                             String dataPrimire, String status) {
            this.clientNumeComplet = clientNumeComplet;
            this.departament = departament;
            this.tip = tip;
            this.producator = producator;
            this.model = model;
            this.serie = serie;
            this.dataPrimire = dataPrimire;
            this.status = status;
        }
    }
}
