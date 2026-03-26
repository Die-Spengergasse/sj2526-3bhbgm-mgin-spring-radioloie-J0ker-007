package at.spengergasse.spring_thymeleaf;

import at.spengergasse.spring_thymeleaf.entities.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class SpringThymeleafApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringThymeleafApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(PatientRepository patientRepository, GeraeteRepository geraeteRepository, ReservationRepository reservationRepository) {
        return args -> {
            if (patientRepository.count() == 0) {
                Patient p1 = new Patient();
                p1.setSsn("123456789");
                p1.setFirstName("Max");
                p1.setLastName("Mustermann");
                p1.setGender("M");
                p1.setBirth(LocalDate.of(1985,1,15));
                patientRepository.save(p1);

                Patient p2 = new Patient();
                p2.setSsn("987654321");
                p2.setFirstName("Erika");
                p2.setLastName("Musterfrau");
                p2.setGender("F");
                p2.setBirth(LocalDate.of(1990,5,20));
                patientRepository.save(p2);
            }

            if (geraeteRepository.count() == 0) {
                Geraete g1 = new Geraete();
                g1.setBezeichnung("MR-1");
                g1.setArt("MR");
                g1.setStandort("101");
                geraeteRepository.save(g1);

                Geraete g2 = new Geraete();
                g2.setBezeichnung("CT-1");
                g2.setArt("CT");
                g2.setStandort("102");
                geraeteRepository.save(g2);
            }

            // optional: eine Beispielreservierung
            if (reservationRepository.count() == 0) {
                Patient p = patientRepository.findAll().get(0);
                Geraete g = geraeteRepository.findAll().get(0);
                Reservation r = new Reservation();
                r.setPatient(p);
                r.setGeraet(g);
                r.setStartTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
                r.setEndTime(r.getStartTime().plusMinutes(30));
                r.setRegion("Kopf");
                r.setComment("Testreservierung");
                reservationRepository.save(r);
            }
        };
    }
}
