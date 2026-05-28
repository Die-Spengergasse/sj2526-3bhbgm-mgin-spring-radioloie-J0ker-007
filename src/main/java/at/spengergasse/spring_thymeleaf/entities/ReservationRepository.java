package at.spengergasse.spring_thymeleaf.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Integer> {
    // Finde alle Reservierungen für ein bestimmtes Gerät
    @Query("SELECT r FROM Reservation r WHERE r.geraet.id = :geraetId")
    List<Reservation> findByGeraetId(@Param("geraetId") int geraetId);

    // Finde alle Reservierungen für einen bestimmten Patienten
    @Query("SELECT r FROM Reservation r WHERE r.patient.id = :patientId")
    List<Reservation> findByPatientId(@Param("patientId") Integer patientId);
}

