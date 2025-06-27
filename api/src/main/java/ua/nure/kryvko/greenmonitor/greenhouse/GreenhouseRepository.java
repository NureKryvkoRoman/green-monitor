package ua.nure.kryvko.greenmonitor.greenhouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.kryvko.greenmonitor.user.User;

import java.util.List;

public interface GreenhouseRepository extends JpaRepository<Greenhouse, Integer> {
    List<Greenhouse> findByUser(User user);

    @Query("SELECT new ua.nure.kryvko.greenmonitor.greenhouse.GreenhouseSummary(" +
            "g.id, " +
            "g.user.id, " +
            "g.name, " +
            "COUNT(DISTINCT s.id), " +
            "SUM(CASE WHEN n.isRead = false THEN 1 ELSE 0 END), " +
            "g.plant.name" +
            ") " +
            "FROM Greenhouse g " +
            "LEFT JOIN g.notifications n " +
            "LEFT JOIN g.sensors s " +
            "WHERE g.user.id = :id " +
            "GROUP BY g.id, g.user.id, g.name, g.plant.name")
    List<GreenhouseSummary> findSummaryByUserId(@Param("id") Integer id);
}
