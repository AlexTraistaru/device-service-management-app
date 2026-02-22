/** Clasa pentru testul de pornire al aplicatiei.
 * testul asta verifica doar ca spring poate porni si incarca contextul (configurari).
 * daca aici pica, inseamna ca avem o problema de configurare
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceDispozitiveApplicationTests {

    // test simplu: daca aplicatia se incarca fara exceptii, testul trece
    @Test
    void contextLoads() {
        // nu facem nimic aici, doar faptul ca metoda se executa inseamna ca spring a pornit
    }

}
