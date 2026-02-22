/** Clasa pentru pornirea aplicatiei spring boot.
 * aici este metoda main care ruleaza aplicatia si incarca tot proiectul (config, controllere, servicii, etc).
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceDispozitiveApplication {

    // punctul de intrare al aplicatiei
    // ruleaza spring si porneste serverul web
    public static void main(String[] args) {
        SpringApplication.run(ServiceDispozitiveApplication.class, args);
    }

}
