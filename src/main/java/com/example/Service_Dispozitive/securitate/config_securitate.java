/** Clasa pentru configurarea securitatii aplicatiei.
 * aici se configureaza:
 * cum se cripteaza parolele
 * cum se cauta utilizatorul din baza de date la login
 * ce rute sunt publice si ce rute sunt protejate pe roluri
 *
 * @author Traistaru Alexandru Mihai
 * @version 12 ianuarie 2026
 */
package com.example.Service_Dispozitive.securitate;

import com.example.Service_Dispozitive.entitati.utilizator;
import com.example.Service_Dispozitive.acces_date.utlizator_acces;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class config_securitate {

    // encoder pentru parole, folosim bcrypt
    // la signup parola este transformata in hash si asa este salvata in baza de date
    @Bean
    public PasswordEncoder encoderParole() {
        return new BCryptPasswordEncoder();
    }

    // spune lui spring security cum incarcam un user la login
    // primeste username, cauta in tabela Utilizatori si intoarce UserDetails
    @Bean
    public UserDetailsService serviciuDetaliiUtilizator(utlizator_acces utilizatori) {
        return (String numeUtilizator) -> {

            // cauta utilizatorul in baza de date dupa username
            utilizator u = utilizatori.findByNumeUtilizator(numeUtilizator)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilizator inexistent: " + numeUtilizator));

            // spring security asteapta rolurile in formatul "ROLE_xxx"
            // in baza de date rolul e "CLIENT" sau "ANGAJAT"
            String rol = "ROLE_" + u.getRol().toUpperCase();

            // construim obiectul UserDetails cu:
            // username
            // parola (hash-ul din baza de date)
            // lista de roluri (authorities)
            return User.withUsername(u.getNumeUtilizator())
                    .password(u.getParola())
                    .authorities(List.of(new SimpleGrantedAuthority(rol)))
                    .build();
        };
    }

    // configureaza regulile de acces pe rute si pagina de login
    @Bean
    public SecurityFilterChain lantFiltre(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        // rute publice (nu cer login)
                        // login si inregistrare trebuie sa fie accesibile fara cont
                        .requestMatchers("/login", "/inregistrare", "/stil.css").permitAll()

                        // rute doar pentru angajat
                        .requestMatchers("/angajat/**").hasRole("ANGAJAT")

                        // rute pentru client sau angajat
                        .requestMatchers("/client/**").hasAnyRole("CLIENT", "ANGAJAT")

                        // rute pentru api (folosite cu postman), doar angajat
                        .requestMatchers("/api/**").hasRole("ANGAJAT")

                        // orice alta ruta cere login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form

                        // pagina de login
                        .loginPage("/login")

                        // dupa login cu succes, mergem pe pagina principala
                        // true inseamna ca mereu trimite acolo, nu la ultima pagina accesata
                        .defaultSuccessUrl("/acasa", true)

                        // daca user/parola sunt gresite, intoarcem pe login cu parametru error
                        .failureUrl("/login?error")

                        .permitAll()
                )
                .logout(l ->

                        // dupa logout, intoarcem pe login cu parametru logout
                        l.logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }
}
